/**
 * Copyright (c) 1996-2004 Borland Software Corp. All Rights Reserved.
 *
 * This SOURCE CODE FILE, which has been provided by Borland as part
 * of a Borland product for use ONLY by licensed users of the product,
 * includes CONFIDENTIAL and PROPRIETARY information of Borland.
 *
 * USE OF THIS SOFTWARE IS GOVERNED BY THE TERMS AND CONDITIONS
 * OF THE LICENSE STATEMENT AND LIMITED WARRANTY FURNISHED WITH
 * THE PRODUCT.
 *
 * IN PARTICULAR, YOU WILL INDEMNIFY AND HOLD BORLAND, ITS RELATED
 * COMPANIES AND ITS SUPPLIERS, HARMLESS FROM AND AGAINST ANY
 * CLAIMS OR LIABILITIES ARISING OUT OF THE USE, REPRODUCTION, OR
 * DISTRIBUTION OF YOUR PROGRAMS, INCLUDING ANY CLAIMS OR LIABILITIES
 * ARISING OUT OF OR RESULTING FROM THE USE, MODIFICATION, OR
 * DISTRIBUTION OF PROGRAMS OR FILES CREATED FROM, BASED ON, AND/OR
 * DERIVED FROM THIS SOURCE CODE FILE.
 */
//--------------------------------------------------------------------------------------------------
// Copyright (c) 1996 - 2004 Borland Software Corporation. All Rights Reserved.
//--------------------------------------------------------------------------------------------------
package com.borland.jbcl.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

/**
 * ImageTexture is a utility class that copies the pixels of a source image
 * onto a larger destination surface - repeating the source image over and over
 * to effectively texture the destination surface.  Image blits are done after
 * calculating the relative position of the origin between the source image and
 * the destination surface.  This allows the methods to be called multiple times
 * passing overlapping or non-contiguous rectangles, and the images will always
 * line up to form a seamless texture.
 */
public class ImageTexture implements java.io.Serializable
{
  /**
   * This method will tile the source image (src) onto the target surface (dest)
   * at 100% scale repeating the image over and over to fill in the specified
   * rectangle (x,y,w,h).  This method uses the (0,0) origin to line up all the
   * image blits so that overlapping or non-contiguous calls to this method will
   * produce a seamless texture.
   *
   * @param src The source image to tile.
   * @param dest The destination Graphics context.
   * @param x The x coordinate of the target rectangle.
   * @param y The y coordinate of the target rectangle.
   * @param w The width of the target rectangle.
   * @param h The height of the target rectangle.
   */
  public static void texture(Image src, Graphics dest, int x, int y, int w, int h) {
    texture(src, -1, -1, dest, 0, 0, x, y, w, h);
  }

  /**
   * This method will tile the source image (src) onto the target surface (dest)
   * at 100% scale repeating the image over and over to fill in the specified
   * rectangle (x,y,w,h).  This method uses the passed (ox,oy) origin to line up
   * all the image blits so that overlapping or non-contiguous calls to this
   * method will produce a seamless texture.
   *
   * @param src The source image to tile.
   * @param dest The destination Graphics context.
   * @param ox The x coordinate of the texture origin point.
   * @param oy The y coordinate of the texture origin point.
   * @param x The x coordinate of the target rectangle.
   * @param y The y coordinate of the target rectangle.
   * @param w The width of the target rectangle.
   * @param h The height of the target rectangle.
   */
  public static void texture(Image src, Graphics dest, int ox, int oy, int x, int y, int w, int h) {
    texture(src, -1, -1, dest, ox, oy, x, y, w, h);
  }

  /**
   * This method will tile the source image (src) onto the target surface (dest)
   * with tiles of the specified size (sw,sh) repeating the image over and over
   * to fill in the specified rectangle (x,y,w,h).  Values less or equal to zero
   * for the tile size will result in that dimension of the image to remain
   * unscaled (100%).  This method uses the (0,0) origin to line up all the image
   * blits so that overlapping or non-contiguous calls to this method will produce
   * a seamless texture.
   *
   * @param src The source image to tile.
   * @param sw The desired scaled width of the tiles.
               Values <= 0 result in an unscaled (100%) dimension.
   * @param sh The desired scaled height of the tiles.
               Values <= 0 result in an unscaled (100%) dimension.
   * @param dest The destination Graphics context.
   * @param x The x coordinate of the target rectangle.
   * @param y The y coordinate of the target rectangle.
   * @param w The width of the target rectangle.
   * @param h The height of the target rectangle.
   */
  public static void texture(Image src, int sw, int sh, Graphics dest, int x, int y, int w, int h) {
    texture(src, sw, sh, dest, 0, 0, x, y, w, h);
  }

  /**
   * All the other flavors of 'texture' just turn around and call this one
   * worker method.<P>
   *
   * This method will tile the source image (src) onto the target surface (dest)
   * with tiles of the specified size (sw,sh) repeating the image over and over
   * to fill in the specified rectangle (x,y,w,h).  Values less or equal to zero
   * for the tile size will result in that dimension of the image to remain
   * unscaled (100%).  This method uses the passed (ox,oy) origin to line up all
   * the image blits so that overlapping or non-contiguous calls to this method
   * will produce a seamless texture.
   *
   * @param src The source image to tile.
   * @param sw The desired scaled width of the tiles.
               Values <= 0 result in an unscaled (100%) dimension.
   * @param sh The desired scaled height of the tiles.
               Values <= 0 result in an unscaled (100%) dimension.
   * @param dest The destination Graphics context.
   * @param ox The x coordinate of the texture origin point.
   * @param oy The y coordinate of the texture origin point.
   * @param x The x coordinate of the target rectangle.
   * @param y The y coordinate of the target rectangle.
   * @param w The width of the target rectangle.
   * @param h The height of the target rectangle.
   */
  public static void texture(Image src, int sw, int sh, Graphics dest, int ox, int oy, int x, int y, int w, int h) {
    // get the height and width of the source image
    int iw = src.getWidth(null);
    int ih = src.getHeight(null);
    // punt if image is not completely loaded
    if (iw < 1 || ih < 1)
      return;
    // calculate the scaled tile sizes
    int tw = sw > 0 ? sw : iw;
    int th = sh > 0 ? sh : ih;
    // acquire and clip the clipping-rectangle of the graphics context
    Rectangle clip = dest.getClipBounds();
    dest.clipRect(x, y, w, h);
    // pop the origin to above-left the target rectangle by stepping back
    // even tile sizes on both axis.
    while (x < ox) ox -= tw;
    while (y < oy) oy -= th;
    // calculate total number of blits (both X and Y directions)
    // required for the tiling entire area starting at the origin
    int xblits = (int)Math.ceil((x - ox + w) / tw);
    int yblits = (int)Math.ceil((y - oy + h) / th);
    // calculate the starting blits
    int xstart = (x - ox) / tw;
    int ystart = (y - oy) / th;
    // loop and blit the texture
    for (int yb = ystart; yb <= yblits; yb++) {
      for (int xb = xstart; xb <= xblits; xb++) {
        // calculate the target points for this blit
        int tx1 = xb * tw + ox;
        int ty1 = yb * th + oy;
        int tx2 = (xb + 1) * tw + ox;
        int ty2 = (yb + 1) * th + oy;
        dest.drawImage(src, tx1, ty1, tx2, ty2, 0, 0, iw, ih, null);
      }
    }
    // restore the original clipping-rectangle
    if (clip != null)
      dest.setClip(clip.x, clip.y, clip.width, clip.height);
  }
}
