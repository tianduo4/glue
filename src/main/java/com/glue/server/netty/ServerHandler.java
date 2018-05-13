package com.glue.server.netty;


import com.alibaba.fastjson.JSONObject;
import com.glue.constant.SystemConstant;
import com.glue.exception.GlueException;
import com.glue.http.HttpRequest;
import com.glue.http.HttpResponse;
import com.glue.http.Route;
import com.glue.ioc.annotation.Path;
import com.glue.router.RouteHandler;
import com.glue.utils.ReflectUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import io.netty.handler.codec.http.*;

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

//    private final ExceptionHandler exceptionHandler = WebContext.blade().exceptionHandler();

    private final static RouteHandler ROUTE_HANDLER = ServerContext.getRouteHandler();


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
//        new RequestExecution(ctx, fullHttpRequest.copy()).run();
        try {
            HttpRequest request = new HttpRequest(fullHttpRequest);
            HttpResponse response=HttpResponse.build(ctx);

            boolean isStatic = false;
            String uri = request.getUri();

////        // write session
////        WebContext.set(new WebContext(request, response));
////
            if (isStaticFile(uri)) {
//            STATIC_FILE_HANDLER.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            Route route = ROUTE_HANDLER.lookupRoute(request.getMethod(), uri);
            if (null == route) {
                log.warn("Not Found\t{}", uri);
                throw new GlueException(404,"Not Found:"+uri);
            }

           log.info("{}\t{}\t{}", request.getProtocol(), request.getMethod(), uri);

//            request.initPathParams(route);
//
//            // get method parameters
//            signature.setRoute(route);

            // middleware
//            if (hasMiddleware && !invokeMiddleware(ROUTE_MATCHER.getMiddleware(), signature)) {
//                this.sendFinish(response);
//                return;
//            }

            // web hook before
//            if (hasBeforeHook && !invokeHook(ROUTE_MATCHER.getBefore(uri), signature)) {
//                this.sendFinish(response);
//                return;
//            }

            // execute
//            signature.setRoute(route);
            this.routeHandle(request,response,route);

            // webHook
//            if (hasAfterHook) {
//                this.invokeHook(ROUTE_MATCHER.getAfter(uri), signature);
//            }
        } catch (Exception e) {
//            if (null != exceptionHandler) {
//                exceptionHandler.handle(e);
//            } else {
//                log.error("Blade Invoke Error", e);
//            }
        } finally {
//            if (!isStatic) this.sendFinish(response);
//            WebContext.remove();
        }



//            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("hello World".getBytes()));
//            response.headers().set(CONTENT_TYPE,new AsciiString("application/json; charset=utf-8"));
//            response.headers().set(TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
//
//            ctx.write(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    /**
     * Actual routing method execution
     *
     * @param signature signature
     */
    public void routeHandle( HttpRequest request, HttpResponse response,Route route) throws Exception {
        Object target = route.getTarget();
        if (null == target) {
            Class<?> clazz = route.getAction().getDeclaringClass();
            target = ServerContext.getIoc().getBean(clazz);
            route.setTarget(target);
        }
//        if (route.getTargetType() == RouteHandler.class) {
//            RouteHandler routeHandler = (RouteHandler) target;
//            routeHandler.handle(signature.request(), signature.response());
//        } else {
            this.handle(request, response,route,new Object[1]);
//        }
    }

    /**
     * handle route signature
     *
     * @param signature route request signature
     * @throws Exception throw like parse param exception
     */
    public void handle(HttpRequest request, HttpResponse response,Route route,Object[] parameters) throws Exception {
        try {
            Method actionMethod = route.getAction();
            Object   target       = route.getTarget();
            Class<?> returnType   = actionMethod.getReturnType();


            Path path = target.getClass().getAnnotation(Path.class);
//            JSON JSON = actionMethod.getAnnotation(JSON.class);
//
//            boolean isRestful = (null != JSON) || (null != path && path.restful());
//
//            // if request is restful and not InternetExplorer userAgent
//            if (isRestful && !signature.request().userAgent().contains(HttpConst.IE_UA)) {
                response.setContentType(SystemConstant.CONTENT_TYPE_JSON);
//            }

            int    len  = actionMethod.getParameterTypes().length;
            Object returnParam = ReflectUtils.invokeMethod(target, actionMethod, len > 0 ? parameters: null);
            if (null == returnParam) return;

//            if (isRestful) {
            String json= JSONObject.toJSONString(returnParam);
            int statusCode=200;
            FullHttpResponse response2 = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.valueOf(statusCode), Unpooled.wrappedBuffer(json.getBytes(CharsetUtil.UTF_8)), false);
//            if (null == this.contentType() && !WebContext.request().isIE()) {
//                this.contentType(C.CONTENT_TYPE_JSON);
//            }
//            this.send(response2);
//            }
//            if (returnType == String.class) {
//                response.render(returnParam.toString());
//                return;
//            }
//            if (returnType == ModelAndView.class) {
//                ModelAndView modelAndView = (ModelAndView) returnParam;
//                response.render(modelAndView);
//            }
            response2.headers().set(HttpHeaderNames.CONTENT_TYPE,new AsciiString("application/json; charset=utf-8"));
            response2.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            response.getCtx().write(response2);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) e = (Exception) e.getCause();
            throw e;
        }
    }


    public void send(@NonNull FullHttpResponse response) {
//        response.headers().set(getDefaultHeader());
//
//        boolean keepAlive = WebContext.request().keepAlive();
//
//        if (!response.headers().contains(HttpConst.CONTENT_LENGTH)) {
//            // Add 'Content-Length' header only for a keep-alive connection.
//            response.headers().set(HttpConst.CONTENT_LENGTH, String.valueOf(response.content().readableBytes()));
//        }
//
//        if (!keepAlive) {
//            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
//        } else {
//            response.headers().set(HttpConst.CONNECTION, KEEP_ALIVE);
//            ctx.write(response, ctx.voidPromise());
//        }
//        isCommit = true;

    }

    private boolean isStaticFile(String uri) {
        return false;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.flush();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (null != exceptionHandler) {
//            exceptionHandler.handle((Exception) cause);
//        } else {
        log.error("Glue Invoke Error", cause);
//        }
        if (ctx.channel().isOpen() && ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.close();
        }
    }

}
