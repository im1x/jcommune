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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * Performs redirection from old phpbb-style URLs to support
 * javatalks.ru forum migration.
 *
 * @author Evgeniy Naumenko
 */
@Controller
public class PhpbbRedirectionController {

    /**
     * Redirects topic URL's, assumes that topic ids
     * haven't been changed during data migration
     *
     * @param id post identifier from url
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/ftopic{id}/**")
    public void showTopic(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/topics/" + id;
        this.setHttp301Headers(response, redirectUrl);
    }

    /**
     * Redirects post URL's, assumes that topic ids
     * haven't been changed during data migration
     *
     * @param id post identifier from url
     * @param response http response object to set headers on
     * @param request http request to figure out the context path
     */
    @RequestMapping("/sutra{id}.php")
    public void showPost(@PathVariable String id, HttpServletResponse response, WebRequest request) {
        String redirectUrl = request.getContextPath() +  "/posts/" + id;
        this.setHttp301Headers(response, redirectUrl);
    }

    /**
     * Method sets Http 301 Moved permanently http headers to
     * indicate this URL should be changed if indexed elsewhere.
     * When used instead of plain redirect it allows graceful
     * search engine index modification. Browser is expected to
     * behave the same way as if redirect response has been sent.
     *
     * @param response http response object to be filled with headers
     * @param newUrl redirect url with context path
     */
    private void setHttp301Headers(HttpServletResponse response, String newUrl) {
        response.setStatus(301);
        response.setHeader("Location", newUrl);
        response.setHeader("Connection", "close");
    }
}
