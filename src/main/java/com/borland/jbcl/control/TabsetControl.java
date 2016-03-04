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

package com.borland.jbcl.control;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;

import com.borland.dx.text.Alignment;
import com.borland.jbcl.model.BasicVectorContainer;
import com.borland.jbcl.model.BasicViewManager;
import com.borland.jbcl.util.BlackBox;
import com.borland.jbcl.util.ImageLoader;
import com.borland.jbcl.view.FocusableItemPainter;
import com.borland.jbcl.view.TabsetView;
import com.borland.jbcl.view.TextItemPainter;

public class TabsetControl extends TabsetView implements BlackBox, java.io.Serializable
{
  public TabsetControl() {
    super();
    super.setModel(new BasicVectorContainer());
    TextItemPainter textPainter = new TextItemPainter(Alignment.LEFT | Alignment.MIDDLE, new Insets(1, 1, 1, 1));
    super.setViewManager(new BasicViewManager(new FocusableItemPainter(textPainter)));
  }

  public void setTextureName(String path) {
    if (path != null && !path.equals("")) {
      Image i = ImageLoader.load(path, this);
      if (i != null) {
        ImageLoader.waitForImage(this, i);
        textureName = path;
        setTexture(i);
      }
      else {
        throw new IllegalArgumentException(path);
      }
    }
    else {
      textureName = null;
      setTexture(null);
    }
  }
  public String getTextureName() {
    return textureName;
  }

  public Dimension getPreferredSize() {
    if (getModel().getCount() > 0)
      return super.getPreferredSize();
    else
      return new Dimension(125, 35);
  }

  protected String textureName;
}
