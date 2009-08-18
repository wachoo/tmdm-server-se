package com.amalto.workbench.image;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import com.amalto.workbench.image.OverlayImage.EPosition;
import com.amalto.workbench.utils.EXObjectStatus;

public class OverlayImageProvider {
    
    public static Image getImageWithStatus(Image source, EXObjectStatus status) {
        ImageDescriptor statusOverlay;
        switch (status) {
        case DEACTIVE:
            statusOverlay = ImageCache.getImage(EImage.WAITING_OVR.getPath());
            break;
        default:
            statusOverlay = null;
            break;
        }
        if(statusOverlay==null) return source;

        OverlayImage overlayImage = new OverlayImage(source, statusOverlay, EPosition.BOTTOM_RIGHT);
        return ImageProvider.getImage(overlayImage);
    }

}
