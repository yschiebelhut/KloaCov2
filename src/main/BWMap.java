package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

/**
 * UI element to show a map of Baden-W&uuml;rttemberg with markers
 */
@SuppressWarnings("serial")
public class BWMap extends JPanel {
	
	/**
	 * Minimum value for latitude of BW (<i>very</i> rough approximation :-)
	 */
   private final static float MIN_LAT = 47.4f;
	
	/**
	 * Maximum value for latitude of BW (<i>very</i> rough approximation :-)
	 */
   private final static float MAX_LAT = 49.9f;
	
	/**
	 * Minimum value for longitude of BW (<i>very</i> rough approximation :-)
	 */
   private final static float MIN_LON = 7.4f;
	
	/**
	 * Maximum value for longitude of BW (<i>very</i> rough approximation :-)
	 */
   private final static float MAX_LON = 10.6f;
	
	/**
	 * Margin around map (in original scale)
	 */
	private final static int MARGIN = 5;
	
	/**
	 * Reference width of map in original scale (already including margin)
	 */
	private final static float MAP_REF_WIDTH = 753 + 2*BWMap.MARGIN;
	
	/**
	 * Reference height of map in original scale (already including margin)
	 */
	private final static float MAP_REF_HEIGHT = 868 + 2*BWMap.MARGIN;
	
	/**
	 * Marker size
	 */
	private final static int MAP_MARKER_SIZE = 20;	
	
	/**
	 * Index in coordinate array of border coordinates
	 */
	private final static int IDX_BORDER = 0;

	/**
	 * Index in coordinate array of enclave B&uuml;singen
	 */
	private final static int IDX_ENCLAVE = 1;
	
	/**
	 * Index in coordinate array of Lake Constance (Bodensee)
	 */
	private final static int IDX_LAKE_CONSTANCE = 2;
	
	/**
    * X parts of the coordinates for border, B&uuml;singen, and Lake Constance.
    * Format for polygon/polyline API
    */
	private final static int[][] X_VALS = {
      { 425, 534, 540, 543, 560, 568, 584, 593, 608, 618, 634, 650, 659, 670, 663, 651, 664, 668, 660, 654, 661, 662, 656, 661, 665, 669, 670, 670, 662, 658, 656, 651, 650, 644, 634, 627, 642, 660, 676, 689, 695, 693, 700, 712, 711, 711, 709, 715, 699, 702, 713, 718, 724, 732, 742, 737, 757, 747, 740, 740, 744, 745, 741, 747, 747, 747, 738, 734, 737, 726, 721, 714, 698, 700, 685, 695, 679, 666, 664, 663, 667, 667, 667, 661, 673, 672, 663, 661, 655, 644, 649, 628, 624, 614, 616, 610, 611, 600, 591, 596, 602, 591, 595, 585, 575, 565, 556, 544, 548, 547, 550, 530, 517, 509, 500, 491, 484, 469, 464, 477, 475, 485, 491, 494, 488, 494, 485, 475, 465, 457, 452, 447, 439, 416, 408, 419, 417, 411, 405, 394, 383, 381, 379, 367, 362, 345, 350, 348, 358, 366, 350, 348, 340, 334, 320, 314, 317, 293, 292, 298, 279, 261, 250, 250, 254, 270, 267, 267, 260, 262, 266, 257, 264, 259, 246, 244, 236, 235, 232, 227, 214, 210, 199, 187, 182, 166, 156, 132, 112, 98, 89, 79, 73, 72, 70, 55, 43, 33, 27, 24, 37, 26, 25, 20, 15, 6, 19, 27, 39, 45, 35, 46, 76, 85, 104, 107, 117, 137, 151, 166, 176, 185, 202, 211, 237, 253, 246, 251, 265, 272, 281, 281, 287, 263, 249, 233, 234, 234, 249, 270, 285, 293, 303, 316, 315, 330, 329, 324, 336, 349, 344, 350, 371 },
		{300,310,306,297},
		{380,375,362,371,388,412,425,433,426,409,404,390,392,400,412,436,442,455,458,465,471,481,498,506,510,515,515,522,534,535,541,546,562,566,570,572,566,553,543,524,520,502,492,492,480,479,466,459,426,423,416,408,388,371,384}
	};

	/**
    * Y parts of the coordinates for border, B&uuml;singen, and Lake Constance.
    * Format for polygon/polyline API
    */
	private final static int[][] Y_VALS = {
      { 797, 833, 830, 826, 827, 822, 811, 800, 804, 807, 798, 798, 803, 789, 776, 759, 752, 745, 728, 727, 723, 708, 694, 687, 676, 669, 653, 640, 629, 616, 601, 590, 576, 563, 547, 536, 507, 500, 506, 495, 493, 488, 483, 482, 472, 460, 452, 450, 432, 421, 422, 420, 433, 420, 415, 430, 419, 404, 399, 386, 375, 366, 361, 350, 344, 335, 318, 316, 311, 302, 293, 294, 287, 280, 267, 248, 235, 230, 211, 207, 205, 192, 180, 179, 158, 153, 126, 116, 100, 105, 109, 123, 122, 119, 102, 93, 84, 99, 93, 88, 69, 54, 47, 33, 37, 31, 43, 40, 32, 20, 7, 22, 17, 8, 13, 11, 11, 15, 25, 28, 33, 39, 33, 44, 51, 56, 62, 61, 57, 65, 80, 85, 88, 92, 91, 102, 111, 105, 106, 115, 114, 116, 131, 135, 145, 152, 141, 128, 127, 113, 116, 105, 108, 108, 96, 81, 67, 73, 78, 96, 102, 79, 85, 96, 130, 136, 147, 148, 154, 159, 166, 175, 185, 193, 205, 217, 231, 245, 261, 269, 297, 302, 311, 318, 328, 362, 376, 389, 422, 432, 475, 486, 512, 544, 549, 572, 595, 609, 624, 655, 678, 693, 706, 726, 763, 778, 805, 826, 819, 824, 833, 844, 835, 824, 829, 840, 836, 839, 837, 823, 817, 820, 819, 834, 836, 831, 829, 818, 814, 825, 819, 809, 806, 800, 805, 798, 792, 782, 762, 755, 749, 762, 759, 768, 781, 797, 786, 781, 783, 786, 789, 805, 802 },
		{792,791,781,782},
		{785,784,776,774,777,789,795,797,776,765,763,749,745,748,757,772,784,796,798,801,804,801,808,808,813,821,826,832,833,836,839,843,846,852,854,860,867,865,867,868,872,871,865,858,849,839,832,824,806,799,802,802,801,802,790}
   };

   /**
    * Rectangle of legend area (gradient fill)
    */
   private final static Rectangle LEGEND_AREA = new Rectangle( 690, 550, 50, 280 );

   /**
    * Linear gradient paint for legend
    */
   private final static LinearGradientPaint LEGEND_PAINT = new LinearGradientPaint( BWMap.LEGEND_AREA.x, BWMap.LEGEND_AREA.y, BWMap.LEGEND_AREA.x, BWMap.LEGEND_AREA.y + BWMap.LEGEND_AREA.height, new float[] { 0, 1 }, new Color[] { Color.GREEN, Color.RED } );
	
   /**
    * Stroke to draw map elements
    */
   private final static Stroke MAP_DRAW_STROKE = new BasicStroke( 3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

   /**
    * Stroke to draw legend elements
    */
   private final static Stroke LEGEND_DRAW_STROKE = new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

	/**
	 * Transformation to apply in order to scale and center map representation to available space
	 */
	private AffineTransform mapTransform;
	
	/**
    * List of markers
    */
   private List<MapMarker> markers = new LinkedList<>();

   /**
    * Create the BW Map
    */
	public BWMap() {
		super.setToolTipText(""); // must be set for location specific tool tip to be triggered
		this.setOpaque(true);
      this.setPreferredSize( new Dimension( 500, 500 ) );

		// pre-calculate scale and translate values on resize events
      this.addComponentListener( new ComponentAdapter() {
			@Override
         public void componentResized(ComponentEvent e) {
            final int width = BWMap.this.getWidth();
            final int height = BWMap.this.getHeight();

				// scale factor
            final float facX = width / BWMap.MAP_REF_WIDTH;
            final float facY = height / BWMap.MAP_REF_HEIGHT;
            final float fac = Math.min( facX, facY );

				// center alignment
				double deltaX = 0;
				double deltaY = 0;
				if ( facX < facY ) {
					deltaY = (height - (BWMap.MAP_REF_HEIGHT*fac))/2;
				}
				else if ( facX > facY ) {
					deltaX = (width - (BWMap.MAP_REF_WIDTH*fac))/2;
				}
				
				// create transformation
				BWMap.this.mapTransform = AffineTransform.getScaleInstance(fac, fac);
				if ( deltaX != 0 || deltaY != 0 ) {
					BWMap.this.mapTransform.preConcatenate(AffineTransform.getTranslateInstance(deltaX, deltaY));
				}
            BWMap.this.repaint();
			}
		});
	}
	
   /**
    * Add item to map
    * 
    * @param item
    *           item to add
    * @param value
    *           total ppp
    */
   public void setMapItem( MapItem item, int value ) {
      if ( item != null ) {
         for ( MapMarker m : this.markers ) {
            // item found (JVM identity good enough for this use case)
            // --> just update value
            if ( m.item == item ) {
               m.setValue( value );
               this.repaint();
               return;
            }
         }
         this.markers.add( new MapMarker( item, value ) );
         this.repaint();
      }
   }

   /**
    * {@inheritDoc}
    */
	@Override
   protected void paintComponent( Graphics g ) {
      super.paintComponent( g );
      final Graphics2D g2 = ((Graphics2D) g);

      // make it a little bit prettier :-)
      g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON );
      g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );

      if ( this.mapTransform != null ) {
         final AffineTransform originalTransform = g2.getTransform();
         final Stroke originalStroke = g2.getStroke();

         // centered and scaled according to available space
         g2.transform( this.mapTransform );

         //
         g2.setStroke( BWMap.MAP_DRAW_STROKE );

         g2.setColor( Color.YELLOW );
         g2.fillPolygon( BWMap.X_VALS[BWMap.IDX_BORDER], BWMap.Y_VALS[BWMap.IDX_BORDER], BWMap.X_VALS[BWMap.IDX_BORDER].length );
         g2.fillPolygon( BWMap.X_VALS[BWMap.IDX_ENCLAVE], BWMap.Y_VALS[BWMap.IDX_ENCLAVE], BWMap.X_VALS[BWMap.IDX_ENCLAVE].length );
         g2.setColor( Color.DARK_GRAY );
         g2.drawPolyline( BWMap.X_VALS[BWMap.IDX_BORDER], BWMap.Y_VALS[BWMap.IDX_BORDER], BWMap.X_VALS[BWMap.IDX_BORDER].length );
         g2.drawPolygon( BWMap.X_VALS[BWMap.IDX_ENCLAVE], BWMap.Y_VALS[BWMap.IDX_ENCLAVE], BWMap.X_VALS[BWMap.IDX_ENCLAVE].length );
         g2.setColor( Color.CYAN );
         g2.fillPolygon( BWMap.X_VALS[BWMap.IDX_LAKE_CONSTANCE], BWMap.Y_VALS[BWMap.IDX_LAKE_CONSTANCE], BWMap.X_VALS[BWMap.IDX_LAKE_CONSTANCE].length );
         g2.setColor( Color.BLUE );
         g2.drawPolygon( BWMap.X_VALS[BWMap.IDX_LAKE_CONSTANCE], BWMap.Y_VALS[BWMap.IDX_LAKE_CONSTANCE], BWMap.X_VALS[BWMap.IDX_LAKE_CONSTANCE].length );

         for ( MapMarker m : this.markers ) {
            g2.setColor( m.fillColor );
            g2.fillRect( m.mapX - 10, m.mapY - 10, 20, 20 );
            g2.setColor( Color.BLACK );
            g2.drawRect( m.mapX - 10, m.mapY - 10, 20, 20 );
         }
         
         g2.setStroke( BWMap.LEGEND_DRAW_STROKE );
         g2.setPaint( BWMap.LEGEND_PAINT );
         g2.fillRect( BWMap.LEGEND_AREA.x, BWMap.LEGEND_AREA.y, BWMap.LEGEND_AREA.width, BWMap.LEGEND_AREA.height );
         g2.setPaint( Color.BLACK );
         g2.drawRect( BWMap.LEGEND_AREA.x, BWMap.LEGEND_AREA.y, BWMap.LEGEND_AREA.width, BWMap.LEGEND_AREA.height );

         g2.setFont( new Font( Font.DIALOG, Font.PLAIN, 18 ) );
         g2.drawString( "0 ppp", BWMap.LEGEND_AREA.x, BWMap.LEGEND_AREA.y - 10 );
         g2.drawString( ">" + (int) MapMarker.MAX_PPP + " ppp", BWMap.LEGEND_AREA.x - 15, BWMap.LEGEND_AREA.y + BWMap.LEGEND_AREA.height + 20 );

         g2.setStroke( originalStroke );
         g2.setTransform( originalTransform );
      }
	}
	
   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTipText( MouseEvent event ) {
      try {
         // convert mouse location back to original space
         final Point2D dst = new Point2D.Double();
         this.mapTransform.inverseTransform( event.getPoint(), dst );

         // search markers backwards (later markers are drawn on top!)
         for ( int i = this.markers.size() - 1; i >= 0; i-- ) {
            MapMarker m = this.markers.get( i );
            if ( m.markerAreaContains( dst ) ) {
               return m.getTitle();
            }
         }
      } catch ( NoninvertibleTransformException e ) {
      }
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setToolTipText( String text ) {
      // ignore (null value would disable tool tips alltogether)
   }

	/**
	 * Map marker wrapper
	 */
	private static class MapMarker {
      /**
       * Max value for PPP that will be threshold to red
       */
      private final static float MAX_PPP = 500f;

		/**
       * The map item
       */
		private MapItem item;

		/**
		 * Map x value (already the top left corner for marker!)
		 */
		private int mapX;
		
		/**
		 * Map y value (already the top left corner for marker!)
		 */
		private int mapY;
		
		/**
       * Value to use
       */
      private Color fillColor;

      /**
       * Total PPP for this marker
       */
      private int value;

      /**
       * Create map marker
       * 
       * @param loc
       *           location object
       */
      public MapMarker( MapItem item, int value ) {
			super();
			this.item = item;
         this.setValue( value );
			this.mapY = this.getMapY(item);
			this.mapX = this.getMapX(item);
		}
		
      /**
       * Check if point matches area of this marker
       * 
       * @param p
       *           point (in origial coordinate space)
       * @return <code>true</code> if matched, <code>false</code> otherwise
       */
      public boolean markerAreaContains( Point2D p ) {
			return p.getX() >= this.mapX && p.getX() < (this.mapX+BWMap.MAP_MARKER_SIZE) && p.getY() >= this.mapY && p.getY() < (this.mapY+BWMap.MAP_MARKER_SIZE);
		}
		
      /**
       * Get map y of top left corner for rectangle.
       * For our simple case, linear interpolation within BW bounding box is
       * sufficient
       * 
       * @param loc
       *           location object
       * @return map y of top left corner for rectangle
       */
		private int getMapY(MapItem item){
         final double percentage = 1 - (item.getLatitude() - BWMap.MIN_LAT) / (BWMap.MAX_LAT - BWMap.MIN_LAT);
         return (int) (percentage * BWMap.MAP_REF_HEIGHT + BWMap.MARGIN) - BWMap.MAP_MARKER_SIZE / 2;
		}
		
      /**
       * Get map x of top left corner for rectangle.
       * For our simple case, linear interpolation within BW bounding box is
       * sufficient.
       * 
       * @param loc
       *           location object
       * @return map x of top left corner for rectangle
       */
		private int getMapX(MapItem item){
         final double percentage = (item.getLongitude() - BWMap.MIN_LON) / (BWMap.MAX_LON - BWMap.MIN_LON);
         return (int) (percentage * BWMap.MAP_REF_WIDTH + BWMap.MARGIN) - BWMap.MAP_MARKER_SIZE / 2;
		}
		
		/**
       * Get title of map marker item
       * 
       * @return title of map marker item
       * @see MapItem#getTitle()
       */
      public String getTitle() {
         return this.item.getTitle() + ", " + this.value + "ppp total";
		}
		
      /**
       * Set value to calculate fill color
       * 
       * @param value
       *           value to set
       */
      public void setValue( int value ) {
         // value=0 -> green, value >= 500 -> red
         float red = 0;
         if ( value > 0 ) {
            if ( value >= MapMarker.MAX_PPP ) {
               red = 1;
            } else {
               red = value / MapMarker.MAX_PPP;
            }
         }
         this.fillColor = new Color( red, 1 - red, 0 );
         this.value = value;
      }
	}
	
}
