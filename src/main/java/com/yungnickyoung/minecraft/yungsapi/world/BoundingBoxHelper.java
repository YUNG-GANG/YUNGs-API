//package com.yungnickyoung.minecraft.yungsapi.world;
//
//import net.minecraft.core.Direction;
//import net.minecraft.world.level.levelgen.structure.BoundingBox;
//
//public class BoundingBoxHelper {
//    /**
//     * Generates and rotates a block box.
//     * The main axis is the primary of the x and z axes, in the direction
//     * the structure should generate from the starting point.
//     */
//    public static BoundingBox boxFromCoordsWithRotation(int x, int y, int z, int secondaryAxisLen, int yLen, int mainAxisLen, Direction mainAxis) {
//        BoundingBox blockBox = new BoundingBox(x, y, z, x, y + yLen - 1, z);
//        switch (mainAxis) {
//            default -> {
//                blockBox.maxX = x + (secondaryAxisLen - 1);
//                blockBox.minZ = z - (mainAxisLen - 1);
//            }
//            case SOUTH -> {
//                blockBox.minX = x - (secondaryAxisLen - 1);
//                blockBox.maxZ = z + (mainAxisLen - 1);
//            }
//            case WEST -> {
//                blockBox.minX = x - (mainAxisLen - 1);
//                blockBox.minZ = z - (secondaryAxisLen - 1);
//            }
//            case EAST -> {
//                blockBox.maxX = x + (mainAxisLen - 1);
//                blockBox.maxZ = z + (secondaryAxisLen - 1);
//            }
//        }
//        return blockBox;
//    }
//}
