/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jtalks.jcommune.model.dao.hibernate;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.joda.time.DateTime;
import org.jtalks.jcommune.model.ObjectsFactory;
import org.jtalks.jcommune.model.PersistedObjectsFactory;
import org.jtalks.jcommune.model.dao.PostDao;
import org.jtalks.jcommune.model.dto.JCommunePageRequest;
import org.jtalks.jcommune.model.entity.JCUser;
import org.jtalks.jcommune.model.entity.Post;
import org.jtalks.jcommune.model.entity.Topic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author Kirill Afonin
 */
@ContextConfiguration(locations = {"classpath:/org/jtalks/jcommune/model/entity/applicationContext-dao.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
@Transactional
public class PostHibernateDaoTest extends AbstractTransactionalTestNGSpringContextTests {

    @Autowired
    private SessionFactory sessionFactory;
    @Autowired
    private PostDao dao;
    private Session session;

    @BeforeMethod
    public void setUp() {
        session = sessionFactory.getCurrentSession();
        PersistedObjectsFactory.setSession(session);
    }

    /*===== Common methods =====*/

    @Test
    public void testGet() {
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);

        Post result = dao.get(post.getId());

        assertNotNull(result);
        assertEquals(result.getId(), post.getId());
    }

    @Test
    public void testGetInvalidId() {
        Post result = dao.get(-567890L);

        assertNull(result);
    }

    @Test
    public void testUpdate() {
        String newContent = "new content";
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(newContent);

        dao.update(post);
        session.evict(post);
        Post result = (Post) session.get(Post.class, post.getId());

        assertEquals(result.getPostContent(), newContent);
    }

    @Test(expectedExceptions = Exception.class)
    public void testUpdateNotNullViolation() {
        Post post = PersistedObjectsFactory.getDefaultPost();
        session.save(post);
        post.setPostContent(null);

        dao.update(post);
    }

    /* PostDao specific methods */

    @Test
    public void testPostOfUserWithEnabledPaging() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize/pageCount;
        
        JCommunePageRequest pageRequest = JCommunePageRequest.createWithPagingEnabled(1, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        JCUser author = posts.get(0).getUserCreated();

        Page<Post> postsPage = dao.getUserPosts(author, pageRequest);

        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
    }

    @Test
    public void testPostsOfUserWithDisabledPaging() {
        int size = 50;
        JCommunePageRequest pageRequest = JCommunePageRequest.createWithPagingDisabled(1, size/2);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(size);
        JCUser author = posts.get(0).getUserCreated();
        
        Page<Post> postsPage = dao.getUserPosts(author, pageRequest);
        
        assertEquals(postsPage.getContent().size(), size, 
                "Paging is disabled, so it should retrieve all posts in the topic.");
        assertEquals(postsPage.getTotalElements(), size, "Incorrect total count.");
    }
    
    @Test
    public void testNullPostOfUser() {
        JCommunePageRequest pageRequest = JCommunePageRequest.createWithPagingEnabled(1, 50);
        JCUser user = ObjectsFactory.getDefaultUser();
        session.save(user);

        Page<Post> postsPage = dao.getUserPosts(user, pageRequest);

        assertFalse(postsPage.hasContent());
    }
    
    @Test
    public void testGetLastPostInTopic() {
        int size = 2;
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(size);
        Topic topic = posts.get(0).getTopic();
        Post expectedLastPost = topic.getPosts().get(size - 1);
        ReflectionTestUtils.setField(
                expectedLastPost,
                "creationDate",
                new DateTime(2100, 12, 25, 0, 0, 0, 0));
        session.save(expectedLastPost);
        
        Post actualLastPost = dao.getLastPostInTopic(topic);
        
        assertNotNull(actualLastPost, "Last post in the topic is not found.");
        assertEquals(actualLastPost.getId(), expectedLastPost.getId(),
                "The last post in the topic is the wrong.");
    }
    
    @Test 
    public void testGetLastPostInEmptyTopic() {
        Topic topic = PersistedObjectsFactory.getDefaultTopic();
        topic.removePost(topic.getFirstPost());
        
        session.save(topic);
        
        assertNull(dao.getLastPostInTopic(topic), "The topic is empty, so the topic should not be found");
    }
    
    @Test
    public void testGetPostsWithEnabledPaging() {
        int totalSize = 50;
        int pageCount = 2;
        int pageSize = totalSize/pageCount;
        JCommunePageRequest pageRequest = JCommunePageRequest.createWithPagingEnabled(1, pageSize);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(totalSize);
        Topic topic = posts.get(0).getTopic();
        
        Page<Post> postsPage = dao.getPosts(topic, pageRequest);
        
        assertEquals(postsPage.getContent().size(), pageSize, "Incorrect count of posts in one page.");
        assertEquals(postsPage.getTotalElements(), totalSize, "Incorrect total count.");
        assertEquals(postsPage.getTotalPages(), pageCount, "Incorrect count of pages.");
    }
    
    @Test
    public void testGetPostsWithDisabledPaging() {
        int size = 50;
        JCommunePageRequest pageRequest = JCommunePageRequest.createWithPagingDisabled(1, size/2);
        List<Post> posts = PersistedObjectsFactory.createAndSavePostList(size);
        Topic topic = posts.get(0).getTopic();
        
        Page<Post> postsPage = dao.getPosts(topic, pageRequest);
        
        assertEquals(postsPage.getContent().size(), size, 
                "Paging is disabled, so it should retrieve all posts in the topic.");
        assertEquals(postsPage.getTotalElements(), size, "Incorrect total count.");
    }
}
