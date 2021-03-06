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
package org.jtalks.jcommune.service.transactional;

import java.util.ArrayList;
import java.util.List;

import org.jtalks.common.model.entity.Branch;
import org.jtalks.common.model.entity.Section;
import org.jtalks.jcommune.model.dao.SectionDao;
import org.jtalks.jcommune.service.BranchService;
import org.jtalks.jcommune.service.SectionService;
import org.jtalks.jcommune.service.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The implementation of SectionService
 *
 * @author Max Malakhov
 */
public class TransactionalSectionService extends AbstractTransactionalEntityService<Section, SectionDao>
        implements SectionService {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private BranchService branchService;
    
    /**
     * Create an instance of entity based service
     *
     * @param dao data access object, which should be able do all CRUD operations.
     * @param branchService autowired object, that represents service for the working with
     *                      branches
     */
    public TransactionalSectionService(SectionDao dao, BranchService branchService) {
        super(dao);
        this.branchService = branchService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Section> getAll() {
        return this.getDao().getAll();
    } 
    
    /**
     * {@inheritDoc}
     */
    public void prepareSectionsForView(List<Section> sections) {
        for(Section section: sections) {
            List<Branch> branches = section.getBranches();
            branchService.fillStatisticInfo(branches);
            branchService.fillLastPostInLastUpdatedTopic(branches);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Section deleteAllTopicsInSection(long sectionId) throws NotFoundException {
        Section section = get(sectionId);
        
        //Create tmp list to avoid ConcurrentModificationException
        List<Branch> loopList = new ArrayList<Branch>(section.getBranches());        
        for (Branch branch : loopList) {
            branchService.deleteAllTopics(branch.getId());
        }
        
        logger.info("All branches for sections \"{}\" were deleted. " +
                "Section id: {}", section.getName(), section.getId());
        return section;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAllTopicsInForum() throws NotFoundException {
        for (Section section : this.getAll()){
             this.deleteAllTopicsInSection(section.getId());
        }
    }
}
