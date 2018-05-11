package com.glue.server.netty;


import com.glue.exception.GlueException;
import com.glue.http.HttpRequest;
import com.glue.http.HttpResponse;
import com.glue.http.Route;
import com.glue.http.RouteHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderNames.TRANSFER_ENCODING;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

//    private final ExceptionHandler exceptionHandler = WebContext.blade().exceptionHandler();

    private final static RouteHandler ROUTE_HANDLER = new RouteHandler();


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

//            Route route = ROUTE_HANDLER.lookupRoute(request.getMethod(), uri);
//            if (null == route) {
//                log.warn("Not Found\t{}", uri);
//                throw new GlueException(404,"Not Found:"+uri);
//            }

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
//            this.routeHandle(signature);

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
