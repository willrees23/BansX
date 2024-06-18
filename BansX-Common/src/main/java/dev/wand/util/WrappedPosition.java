package dev.wand.util;

import lombok.Data;

/**
 * @author Salers
 * made on dev.wand.util
 */

@Data
public class WrappedPosition {

    private final double x,y,z;
    private final String world;
}
