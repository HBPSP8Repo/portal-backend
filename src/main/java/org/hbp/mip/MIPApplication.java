/**
 * Created by mirco on 04.12.15.
 * Based on gregturn code at : 'https://github.com/spring-guides/tut-spring-boot-oauth2'.
 */

/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hbp.mip;

import org.hbp.mip.mock.ArticleMock;
import org.hbp.mip.mock.ModelMock;
import org.hbp.mip.model.*;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@RestController
@EnableOAuth2Client
public class MIPApplication extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @RequestMapping("/user")
    @ResponseBody
    public Principal user(Principal principal) {
        return principal;
    }

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    @ResponseBody
    public List<Article> getArticles() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List articles = session.createQuery("from Article").list();
        session.getTransaction().commit();
        return articles;
    }

    @RequestMapping(value = "/articles/{slug}", method = RequestMethod.GET)
    @ResponseBody
    public Article getArticle(@PathVariable("slug") String slug) {
        return new ArticleMock(1);
    }

    @RequestMapping(value = "/datasets/{code}", method = RequestMethod.GET)
    @ResponseBody
     public Dataset getDatasets(@PathVariable("code") String code) {
        return null;
    }

    @RequestMapping(value = "/models", method = RequestMethod.GET)
    @ResponseBody
    public List<Model> getModels() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List models = session.createQuery("from Model").list();
        session.getTransaction().commit();
        return models;
    }

    @RequestMapping(value = "/models/{slug}", method = RequestMethod.GET)
    @ResponseBody
    public Model getModel(@PathVariable("slug") String slug) {
        return new ModelMock(1);
    }

    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    @ResponseBody
    public Article postArticle(@RequestBody Article article) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        article.setCreatedAt(new Date());
        article.setPublishedAt(new Date());
        article.setSlug(article.getTitle().toLowerCase());
        article.setStatus("published");
        session.save(article);
        session.getTransaction().commit();
        return article;
    }

    @RequestMapping(value = "/models", method = RequestMethod.POST)
    @ResponseBody
    public Model postModel(@RequestBody Model model) {
        return new ModelMock(1);
    }

    @RequestMapping(value = "/models/{slug}/copies", method = RequestMethod.POST)
    @ResponseBody
    public Model postModelCopies(@PathVariable("slug") String slug) {
        return new ModelMock(1);
    }

    @RequestMapping(value = "/queries/requests", method = RequestMethod.POST)
    @ResponseBody
    public Query postRequest(@RequestBody Query query) {
        return null;
    }

    @RequestMapping(value = "/articles/{slug}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void putArticle(@PathVariable("slug") String slug) {
    }

    @RequestMapping(value = "/models/{slug}", method = RequestMethod.PUT)
    @ResponseBody
    public Model putModel(@PathVariable("slug") String slug) {
        return null;
    }

    @RequestMapping(value = "/articles/{slug}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArticle(@PathVariable("slug") String slug) {
    }

    @RequestMapping(value = "/models/{slug}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteModel(@PathVariable("slug") String slug, @RequestBody Model model) {
    }

    @RequestMapping(value = "/groups")
    @ResponseBody
    public Group getGroups(){
        return null;
    }

    @RequestMapping(value = "/variables")
    @ResponseBody
    public List<Variable> getVariables(){
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List variables = session.createQuery("from Variable").list();
        session.getTransaction().commit();
        return variables;
    }

    @RequestMapping(value = "/variables/{code}")
    @ResponseBody
    public Variable getVariable(@PathVariable("code") String code){
        return null;
    }

    @RequestMapping(value = "/variables/{code}/values")
    @ResponseBody
    public List<Value> getValues(@PathVariable("code") String code){
        return null;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.antMatcher("/**")
                .authorizeRequests()
                .antMatchers("/", "/frontend/**", "/webjars/**").permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/"))
                .and().logout().logoutSuccessUrl("/").permitAll()
                .and().csrf().csrfTokenRepository(csrfTokenRepository())
                .and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @formatter:on
    }

    public static void main(String[] args) {
        SpringApplication.run(MIPApplication.class, args);
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(
            OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter hbpFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/hbp");
        OAuth2RestTemplate hbpTemplate = new OAuth2RestTemplate(hbp(), oauth2ClientContext);
        hbpFilter.setRestTemplate(hbpTemplate);
        hbpFilter.setTokenServices(new UserInfoTokenServices(hbpResource().getUserInfoUri(), hbp().getClientId()));
        return hbpFilter;
    }

    @Bean
    @ConfigurationProperties("hbp.client")
    OAuth2ProtectedResourceDetails hbp() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("hbp.resource")
    ResourceServerProperties hbpResource() {
        return new ResourceServerProperties();
    }

    private Filter csrfHeaderFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
                if (csrf != null) {
                    Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
                    String token = csrf.getToken();
                    if (cookie == null || token != null && !token.equals(cookie.getValue())) {
                        cookie = new Cookie("XSRF-TOKEN", token);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }

}
