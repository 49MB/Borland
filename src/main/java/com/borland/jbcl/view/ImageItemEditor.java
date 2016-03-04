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
package com.borland.jbcl.view;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.KeyListener;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.borland.dx.dataset.Variant;
import com.borland.jb.io.InputStreamToByteArray;
import com.borland.jb.util.Diagnostic;
import com.borland.jbcl.model.ItemEditSite;
import com.borland.jbcl.model.ItemEditor;

public class ImageItemEditor extends Canvas implements ItemEditor,
    java.io.Serializable {
  public ImageItemEditor() {
    variant = new Variant();
    variant.setInputStream(null);
  }
  
  public Component getComponent() {
    return this;
  }
  
  public void startEdit(Object data, Rectangle bounds, ItemEditSite site) {
    Component component = site != null ? site.getSiteComponent() : this;
    while (component != null && !(component instanceof Frame))
      component = component.getParent();
    if (component instanceof Frame) {
      FileDialog dialog = new FileDialog((Frame) component,
          Res._SelectImageFile, FileDialog.LOAD);
      dialog.setDirectory(null);
      dialog.show();
      String dir = dialog.getDirectory();
      String file = dialog.getFile();
      if (dir != null && file != null) {
        String name = dir + file;
        try {
          FileInputStream stream = new FileInputStream(name);
          byte[] bytes = byteArrayFromStream(stream);
          variant.setInputStream(new ByteArrayInputStream(bytes));
          site.safeEndEdit(true);
        } catch (Exception ex) {
          Diagnostic.printStackTrace(ex);
          Diagnostic.fail();
        }
      } else {
        // dialog was cancelled
        site.safeEndEdit(false);
      }
    } else
      throw new RuntimeException(Res._FileDialogNoFrame);
  }
  
  private static byte[] byteArrayFromStream(InputStream s) throws IOException {
    return InputStreamToByteArray.getBytes(s);
  }
  
  public Object getValue() {
    return variant.getInputStream();
  }
  
  public void changeBounds(Rectangle bounds) {
  }
  
  public boolean canPost() {
    return true;
  }
  
  public void endEdit(boolean posted) {
  }
  
  @Override
  public void addKeyListener(KeyListener l) {
    // No Key Events!
  }
  
  @Override
  public void removeKeyListener(KeyListener l) {
    // No Key Events!
  }
  
  private final Variant variant;
}
