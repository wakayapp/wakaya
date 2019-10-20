package com.github.wakayapp.wakaya;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.MotionEvent;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import java.util.concurrent.CompletableFuture;

/**
 * Node for rendering an augmented image. The image is framed by placing the virtual picture frame
 * at the corners of the augmented image trackable.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageNodeSingle extends AnchorNode {

    private static final String TAG = "AugmentedImageNodeSingle";

    // The augmented image represented by this node.
    private AugmentedImage image;

    // Single block to render
    private Node singleNode;

    // Add a variable called singleRenderable for use with loading
    // GreenMaze.sfb.
    private CompletableFuture<ModelRenderable> singleRenderable;

    // Replace the definition of the AugmentedImageNode function with the
    // following code, which loads SeaTurtle.sfb into singleRenderable.
    public AugmentedImageNodeSingle(Context context) {
        singleRenderable =
                ModelRenderable.builder()
                        .setSource(context, Uri.parse("sea_turtle.sfb"))
                        .build();
    }

    // Replace the definition of the setImage function with the following
    // code, which checks if singleRenderable has completed loading.

    public void setImage(AugmentedImage image) {
        this.image = image;

        // Initialize singleNode and set its parents and the Renderable.
        // If any of the models are not loaded, process this function
        // until they all are loaded.
        if (!singleRenderable.isDone()) {
            CompletableFuture.allOf(singleRenderable)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
            return;
        }

        // Set the anchor based on the center of the image.
        setAnchor(image.createAnchor(image.getCenterPose()));

        singleNode = new Node();
        singleNode.setParent(this);
        singleNode.setRenderable(singleRenderable.getNow(null));

        singleNode.setOnTapListener(
                (HitTestResult hitTestResult, MotionEvent motionEvent) -> {
                    Log.i(TAG,  ">>>>>>>>>>>>>>>>>>>>> Hit the node!");
                });
    }

    public AugmentedImage getImage() {
        return image;
    }
}
