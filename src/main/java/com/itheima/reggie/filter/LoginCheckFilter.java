package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.String;

/**
 * 检查用户是否已经完成登录的过滤器
 */
// urlPatterns="/*"表示此拦截器，要拦截所有的路径
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter  implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}",request.getRequestURI());
        /**
         * 1.获取本次请求的URL
         * 2.判断本次请求是否需要处理
         * 3.如果不需要处理，则直接放行
         * 4.判断登录状态，如果已登录，则放行
         * 5.如果未登录，则返回未登录的结果
         */
//        1.获取本次请求的URL
        String requestURI = request.getRequestURI();
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/user/login",
                "user/logout",
                "/backend/**",
                "/front/**"
        };
//        2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
//      3.如果不需要处理，则直接放行
        if (check){
            //匹配成功，不需要处理，直接放行
            log.info("本次请求不需要处理:{}",requestURI);
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }else{
            //匹配失败，需要处理，进行拦截
            log.info("本次请求需要处理:{}",requestURI);
//            String uid =(String) request.getSession().getAttribute("UID");
            Object uid = request.getSession().getAttribute("UID");

            if (uid!=null){
                //用户已登录，直接放行
                log.info("用户已登录，放行");
                log.info("用户的id={}",uid);
                //将当前用户登录的id封装到ThreadLocal中，在MyMateObjectHandler中调用
                BaseContext.setCurrentId((Long) uid);
                filterChain.doFilter(servletRequest,servletResponse);
                return;
            }else {
                //用户没有登录，通过输出流的方式，向前端返回数据
                log.info("用户未登录，向客户端返回NOTLOGIN数据");
                response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            }
        }

    }
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                //匹配成功
                return true;
            }
        }
        //匹配失败
        return false;
    }

}
