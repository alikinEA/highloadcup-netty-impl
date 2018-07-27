package server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import models.Result;
import service.Service;

import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Created by Alikin E.A. on 18.05.18.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH =  new AsciiString("Content-Length");
    private static final AsciiString CONNECTION =  new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE =  new AsciiString("keep-alive");
    private static final AsciiString SERVER =  new AsciiString("Server");
    private static final AsciiString SERVER_VALUE =  new AsciiString("netty");
    private static final AsciiString CONTENT_TYPE_VALUE =  new AsciiString("application/json; charset=UTF-8");


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof FullHttpRequest) {
                FullHttpRequest req = (FullHttpRequest) msg;

                Result result = Service.handle(req);
                FullHttpResponse response = null;
                if (result.getStatus().equals(HttpResponseStatus.OK)) {
                    response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(result.getContent()));
                }
                if (result.getStatus().equals(HttpResponseStatus.NOT_FOUND)) {
                    response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
                }
                if (result.getStatus().equals(HttpResponseStatus.BAD_REQUEST)) {
                    response = new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST);
                }

                response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
                response.headers().set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
                response.headers().set(SERVER, SERVER_VALUE);

                ctx.write(response).addListener(ChannelFutureListener.CLOSE);
            }
        } catch (Exception e) {
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND);
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            response.headers().set(CONTENT_TYPE, CONTENT_TYPE_VALUE);
            response.headers().set(SERVER, SERVER_VALUE);
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
