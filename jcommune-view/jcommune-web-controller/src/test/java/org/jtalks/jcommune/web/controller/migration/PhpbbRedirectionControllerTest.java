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
package org.jtalks.jcommune.web.controller.migration;

import org.mockito.Mock;
import org.springframework.web.context.request.WebRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PhpbbRedirectionControllerTest {

    @Mock
    private HttpServletResponse response;
    @Mock
    private WebRequest request;

    private PhpbbRedirectionController controller;

    private static final String CONTEXT = "/context";
    
    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        when(request.getContextPath()).thenReturn(CONTEXT);
        controller = new PhpbbRedirectionController();
    }

    @Test
    public void testShowTopic() {
       controller.showTopic("1", response, request);
       this.assertHeadersSet(CONTEXT + "/topics/1");
    }

    @Test
    public void testShowPost() {
        controller.showPost("1", response, request);
        this.assertHeadersSet(CONTEXT + "/posts/1");
    }

    private void assertHeadersSet(String redirectUrl) {
        verify(response).setStatus(301);
        verify(response).setHeader("Location", redirectUrl);
        verify(response).setHeader("Connection", "close");
    }
}
