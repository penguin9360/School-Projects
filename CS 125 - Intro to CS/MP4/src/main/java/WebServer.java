import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * A class that runs a small web server which calls your image transformation functions.
 * <p>
 * This server passes asynchronous POST calls made by the JavaScript running on the home page
 * through to the image transformation functions defined in <code>Transform.java</code>. You are
 * welcome to peruse this file, but you should not have to modify it. Note that the web server is
 * <i>not</i> used by the testing suite, which calls your image transformation functions directly.
 *
 * @see <a href="https://cs125.cs.illinois.edu/MP/4/">MP4 Documentation</a>
 * @see <a href="http://vertx.io/docs/vertx-web/java/">Vert.x-Web Documentation</a>
 */
public class WebServer {

    /**
     * The port that our server listens on.
     */
    private static final int DEFAULT_SERVER_PORT = 8125;

    /**
     * Wrap the image transform functions.
     * <p>
     * This function first deserializes the data sent by the web client, then calls the appropriate
     * transformation function, and then serializes the result.
     * <p>
     * Some of the transformation and bit operations performed by this function may be useful for
     * you to review as you write your own transformation functions.
     *
     * @param routingContext the Vert.x routing context for this request
     * @see <a href="https://en.wikipedia.org/wiki/RGBA_color_space">RGBA Wikipedia Article</a>
     */
    private static void wrapImageTransform(final RoutingContext routingContext) {

        /*
         * Pull fields off of the request. We have a width, a height, and a flat array of image
         * bytes. The image bytes are encoded using Base64, but getBinary will take care of that for
         * us.
         */
        JsonObject uploadContent = routingContext.getBodyAsJson();
        int width = uploadContent.getInteger("width");
        int height = uploadContent.getInteger("height");
        byte[] data = uploadContent.getBinary("data");

        /*
         * Unflatten the data array. Data is a flat array of 8-bit pixel values. Indexing is left to
         * right and top to bottom, hence the reversed (y then x) for loop. For each pixel, the
         * bytes are the red, green, blue, and alpha value, in that order (RGBA).
         */
        RGBAPixel[][] pixels = new RGBAPixel[width][height];
        int dataIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[x][y] = new RGBAPixel(data[dataIndex++],
                        data[dataIndex++],
                        data[dataIndex++],
                        data[dataIndex++]);
            }
        }

        /*
         * Routing table. There should be a less terrible way to do this, but in Java it gets
         * complicated quickly. This is sufficient for our purposes.
         */
        String transformation = routingContext.request().getParam("transformation");

        /*
         * By default just return the original array.
         */
        RGBAPixel[][] transformedPixels = pixels;

        try {
            switch (transformation) {
                case "shiftLeft":
                    transformedPixels = Transform.shiftLeft(pixels,
                            Transform.DEFAULT_POSITION_SHIFT);
                    break;
                case "shiftRight":
                    transformedPixels = Transform.shiftRight(pixels,
                            Transform.DEFAULT_POSITION_SHIFT);
                    break;
                case "shiftUp":
                    transformedPixels = Transform.shiftUp(pixels,
                            Transform.DEFAULT_POSITION_SHIFT);
                    break;
                case "shiftDown":
                    transformedPixels = Transform.shiftDown(pixels,
                            Transform.DEFAULT_POSITION_SHIFT);
                    break;
                case "rotateLeft":
                    transformedPixels = Transform.rotateLeft(pixels);
                    break;
                case "rotateRight":
                    transformedPixels = Transform.rotateRight(pixels);
                    break;
                case "flipVertical":
                    transformedPixels = Transform.flipVertical(pixels);
                    break;
                case "flipHorizontal":
                    transformedPixels = Transform.flipHorizontal(pixels);
                    break;
                case "shrinkVertical":
                    transformedPixels = Transform.shrinkVertical(pixels,
                            Transform.DEFAULT_RESIZE_AMOUNT);
                    break;
                case "expandVertical":
                    transformedPixels = Transform.expandVertical(pixels,
                            Transform.DEFAULT_RESIZE_AMOUNT);
                    break;
                case "shrinkHorizontal":
                    transformedPixels = Transform.shrinkHorizontal(pixels,
                            Transform.DEFAULT_RESIZE_AMOUNT);
                    break;
                case "expandHorizontal":
                    transformedPixels = Transform.expandHorizontal(pixels,
                            Transform.DEFAULT_RESIZE_AMOUNT);
                    break;
                case "moreRed":
                    transformedPixels = Transform.moreRed(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "lessRed":
                    transformedPixels = Transform.lessRed(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "moreGreen":
                    transformedPixels = Transform.moreGreen(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "lessGreen":
                    transformedPixels = Transform.lessGreen(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "moreBlue":
                    transformedPixels = Transform.moreBlue(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "lessBlue":
                    transformedPixels = Transform.lessBlue(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "moreAlpha":
                    transformedPixels = Transform.moreAlpha(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "lessAlpha":
                    transformedPixels = Transform.lessAlpha(pixels,
                            Transform.DEFAULT_COLOR_SHIFT);
                    break;
                case "greenScreen":
                    transformedPixels = Transform.greenScreen(pixels);
                    break;
                case "mystery":
                    transformedPixels = Transform.mystery(pixels);
                    break;
                default:
                    System.out.println("Got an invalid transformation: " + transformation);
                    transformedPixels = pixels;
                    break;
            }
        } catch (Exception e) {
            System.out.println("Something went wrong: " + e);
        }

        /*
         * Sanity check the returned array.
         */
        if (transformedPixels.length == 0 || transformedPixels[0].length == 0 //
                || transformedPixels.length != width || transformedPixels[0].length != height) {
            throw new RuntimeException("Bad dimensions for transformed array");
        }

        /*
         * Flatten the array returned by the transformation function. Opposite of the unflattening
         * we performed above.
         */
        byte[] transformedData = new byte[data.length];
        dataIndex = 0;
        for (int y = 0; y < transformedPixels[0].length; y++) {
            for (int x = 0; x < transformedPixels.length; x++) {
                transformedData[dataIndex++] = (byte) transformedPixels[x][y].getRed();
                transformedData[dataIndex++] = (byte) transformedPixels[x][y].getGreen();
                transformedData[dataIndex++] = (byte) transformedPixels[x][y].getBlue();
                transformedData[dataIndex++] = (byte) transformedPixels[x][y].getAlpha();
            }
        }

        /*
         * Just overwrite the original data array, since this JSON object already contains the
         * correct width and height.
         */
        uploadContent.put("data", transformedData);

        /*
         * Send transformed data back to the client as a JSON object.
         */
        routingContext.response()
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(uploadContent.encode());
    }

    /**
     * Start the transformation web server.
     * <p>
     * Note that the main method is not used during testing. So you are free to both understand and
     * modify this function.
     *
     * @param unused unused input arguments
     */
    public static void main(final String[] unused) {

        Vertx vertx = Vertx.vertx();

        /*
         * Set up routes to our static assets: index.html, index.js, and index.css. We use a single
         * route here for all GET requests. In a more complex web server this would probably not be
         * appropriate, but in this simple case it works fine.
         */
        Router router = Router.router(vertx);
        router.route().method(HttpMethod.GET).handler(StaticHandler.create());

        /*
         * The BodyHandler ensures that we can retrieve JSON data from our request body. We send all
         * POST requests to the handler defined above. Again, in a more complex server you would
         * want to do something more sophisticated.
         */
        router.route().method(HttpMethod.POST).handler(BodyHandler.create());
        router.route(HttpMethod.POST, "/:transformation").handler(WebServer::wrapImageTransform);

        /*
         * Turn on compression, although upstream compression isn't currently implemented.
         */
        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setCompressionSupported(true);
        serverOptions.setDecompressionSupported(true);

        HttpServer server = vertx.createHttpServer();

        /*
         * Ensure that the server is closed when we exit, to avoid port collisions.
         */
        Runtime.getRuntime().addShutdownHook(new Thread(vertx::close));

        /*
         * Start the server.
         */
        System.out.println("Starting web server on localhost:" + DEFAULT_SERVER_PORT);
        System.out.println("If you get a message about a port in use, please shut\n"
                + "down other running instances of this web server.");
        server.requestHandler(router::accept).listen(DEFAULT_SERVER_PORT);
    }
}
