import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.scene.paint.Color;
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.geometry.Insets;
import javafx.geometry.Bounds;


// --------
// JFXMeter
// --------
/**
 * @brief  The main window of this application.
 */
public class JFXMeter extends Application
{

  // ----
  // main
  // ----
  /**
   * @brief  The application starts here.
   */
  public static void main(String[] args)
  {
    launch(args);
  }

  // --------
  // JFXMeter
  // --------
  /**
   * @brief  The constructor initializes some attributes.
   */
  public JFXMeter()
  {
    m_initialWidth   = 400;
    m_initialHeight  = 225;
    m_initialOpacity = 0.72;
    m_borderFac      = 4;
    m_borderSize     = 15;
    m_opacityMin     = 0.2;
    m_opacityInc     = 0.02;
    m_deltaSlow      = 1.0;
    m_deltaDefault   = 10.0;
    m_deltaFast      = 50.0;
    m_delayPre       = 300;
    m_delayPost      = 700;
    m_mouseX         = 0.0;
    m_mouseY         = 0.0;
    m_stageX         = 0.0;
    m_stageY         = 0.0;
    m_stageW         = 0.0;
    m_stageH         = 0.0;
    m_northReturn    = false;
    m_northReturnX   = 0.0;
    m_northReturnY   = 0.0;
    m_northReturnW   = 0.0;
    m_northReturnH   = 0.0;
    m_northReturnO   = 0.0;

    m_corner = new Background
    (
      new BackgroundFill
      (
        Color.RED,
        CornerRadii.EMPTY,
        Insets.EMPTY
      )
    );

    m_edge = new Background
    (
      new BackgroundFill
      (
        Color.rgb(96, 96, 96),
        CornerRadii.EMPTY,
        Insets.EMPTY
      )
    );
  }

  // -----
  // start
  // -----
  /**
   * @brief  This method initializes the primary stage.
   */
  public void start(Stage stage) throws Exception
  {
    m_stage = stage;

    m_north     = createHorizontalRegion (m_borderSize);
    m_northEast = createSquareRegion     (m_borderSize);
    m_east      = createVerticalRegion   (m_borderSize);
    m_southEast = createSquareRegion     (m_borderSize);
    m_south     = createHorizontalRegion (m_borderSize);
    m_southWest = createSquareRegion     (m_borderSize);
    m_west      = createVerticalRegion   (m_borderSize);
    m_northWest = createSquareRegion     (m_borderSize);
  
    m_center = new Region();
    m_center.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
    m_center.setBackground( new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)) );
  
    GridPane layout = createGridPane();
    layout.add(m_northWest, 0, 0);
    layout.add(m_north,     1, 0);
    layout.add(m_northEast, 2, 0);
    layout.add(m_west,      0, 1);
    layout.add(m_center,    1, 1);
    layout.add(m_east,      2, 1);
    layout.add(m_southWest, 0, 2);
    layout.add(m_south,     1, 2);
    layout.add(m_southEast, 2, 2);

    Scene root = new Scene(layout, m_initialWidth, m_initialHeight);

    // scene events
    root.setOnMousePressed(e->{ onLeftMouseButtonPressed(e); });
    root.setOnScroll(e->{ onMouseWheel(e); });
    root.setOnKeyPressed(e->{
      switch (e.getCode())
      {
        case ENTER   : onSendMessage(); break;
        case UP      : onUPkey(e);      break;
        case DOWN    : onDOWNkey(e);    break;
        case LEFT    : onLEFTkey(e);    break;
        case RIGHT   : onRIGHTkey(e);   break;
        case PLUS    : onPLUSkey(e);    break;
        case ADD     : onPLUSkey(e);    break;
        case MINUS   : onMINUSkey(e);   break;
        case SUBTRACT: onMINUSkey(e);   break;
      }
      e.consume();
    });

    // drag events
    m_north     .setOnMouseDragged(e->{ onNorthDragged(e);     });
    m_northEast .setOnMouseDragged(e->{ onNorthEastDragged(e); });
    m_east      .setOnMouseDragged(e->{ onEastDragged(e);      });
    m_southEast .setOnMouseDragged(e->{ onSouthEastDragged(e); });
    m_south     .setOnMouseDragged(e->{ onSouthDragged(e);     });
    m_southWest .setOnMouseDragged(e->{ onSouthWestDragged(e); });
    m_west      .setOnMouseDragged(e->{ onWestDragged(e);      });
    m_northWest .setOnMouseDragged(e->{ onNorthWestDragged(e); });
    m_center    .setOnMouseDragged(e->{ onCenterDragged(e);    });

    // double click events
    m_north     .setOnMouseClicked(e->{ onNorthDoubleClicked(e);     });
    m_northEast .setOnMouseClicked(e->{ onNorthEastDoubleClicked(e); });
    m_east      .setOnMouseClicked(e->{ onEastDoubleClicked(e);      });
    m_south     .setOnMouseClicked(e->{ onSouthDoubleClicked(e);     });
    m_center    .setOnMouseClicked(e->{ onCenterDoubleClicked(e);    });

    // initialize primary stage
    m_stage.initStyle(StageStyle.UNDECORATED);
    m_stage.setScene(root);
    m_stage.setOpacity(m_initialOpacity);
    m_stage.show();
  }


  // ---------------------------------------------------------------------------
  // Internal methods                                           Internal methods
  // ---------------------------------------------------------------------------

  // --------------
  // createGridPane
  // --------------
  /**
   * @brief  This method creates the basic layout.
   */
  private GridPane createGridPane()
  {
    GridPane layout = new GridPane();

    layout.setHgap(0);
    layout.setVgap(0);
    layout.setPadding( new Insets(0) );

    RowConstraints r1 = new RowConstraints();
    RowConstraints r2 = new RowConstraints();
    RowConstraints r3 = new RowConstraints();
    r1.setVgrow(Priority.NEVER);
    r2.setVgrow(Priority.ALWAYS);
    r3.setVgrow(Priority.NEVER);
    layout.getRowConstraints().addAll(r1, r2, r3);

    ColumnConstraints c1 = new ColumnConstraints();
    ColumnConstraints c2 = new ColumnConstraints();
    ColumnConstraints c3 = new ColumnConstraints();
    c1.setHgrow(Priority.NEVER);
    c2.setHgrow(Priority.ALWAYS);
    c3.setHgrow(Priority.NEVER);
    layout.getColumnConstraints().addAll(c1, c2, c3);

    return layout;
  }

  // ----------------------
  // createHorizontalRegion
  // ----------------------
  /**
   * @brief  This method can create 'north' and 'south' regions.
   */
  private Region createHorizontalRegion(int height)
  {
    Region region = new Region();
    region.setMinHeight  (height);
    region.setMaxHeight  (height);
    region.setPrefHeight (height);
    region.setMinWidth   (height);
    region.setMaxWidth   (Double.MAX_VALUE);
    region.setBackground (m_edge);
    return region;
  }

  // --------------------
  // createVerticalRegion
  // --------------------
  /**
   * @brief  This method can create 'east' and 'west' regions.
   */
  private Region createVerticalRegion(int width)
  {
    Region region = new Region();
    region.setMinWidth   (width);
    region.setMaxWidth   (width);
    region.setPrefWidth  (width);
    region.setMinHeight  (width);
    region.setMaxHeight  (Double.MAX_VALUE);
    region.setBackground (m_edge);
    return region;
  }

  // ------------------
  // createSquareRegion
  // ------------------
  /**
   * @brief  This method can create 'corner' regions.
   */
  private Region createSquareRegion(int size)
  {
    Region region = new Region();
    region.setMinSize    (size, size);
    region.setMaxSize    (size, size);
    region.setPrefSize   (size, size);
    region.setBackground (m_corner);
    return region;
  }

  // -------------
  // onSendMessage
  // -------------
  /**
   * @brief  This method creates and sends the message, that will be seen on stdout.
   */
  private void onSendMessage()
  {
    try
    {
      int x = 0;
      int y = 0;
      int w = 0;
      int h = 0;

      double opc = m_stage.getOpacity();

      if (opc < 1.0)
      {
        Bounds b = m_center.localToScreen( m_center.getBoundsInLocal() );
        x = (int)Math.round(b.getMinX());
        y = (int)Math.round(b.getMinY());
        w = (int)Math.round(b.getWidth());
        h = (int)Math.round(b.getHeight());
      }

      else
      {
        x = (int)Math.round(m_stage.getX());
        y = (int)Math.round(m_stage.getY());
        w = (int)Math.round(m_stage.getWidth());
        h = (int)Math.round(m_stage.getHeight());
      }

      m_stage.setOpacity(0.0);
  
      Platform.runLater
      (
        new WaitPrintShow(m_delayPre, m_delayPost, m_stage, opc, x, y, w, h)
      );
    }

    catch (Exception any) { /* nothing */ }
  }

  // -------
  // onUPkey
  // -------
  /**
   * @brief  This method handles the UP key event.
   */
  private void onUPkey(KeyEvent e)
  {
    double                 dy = -m_deltaDefault;
    if (e.isAltDown())     dy = -m_deltaSlow; else
    if (e.isControlDown()) dy = -m_deltaFast;

    if (e.isShiftDown())
    {
      double h = m_stage.getHeight();
      double r = h + dy;

      if (r < (m_borderFac * m_borderSize))
      {
        dy = (m_borderFac * m_borderSize) - h;

        if (dy >= 0) return;
      }

      m_stage.setHeight(h + dy);
    }

    else
    {
      double y = m_stage.getY();
      double r = y + dy;

      if (r < 0)
      {
        dy -= r;

        if (dy >= 0) return;
      }

      m_stage.setY(y + dy);
    }
  }

  // ---------
  // onDOWNkey
  // ---------
  /**
   * @brief  This method handles the DOWN key event.
   */
  private void onDOWNkey(KeyEvent e)
  {
    double                 dy = m_deltaDefault;
    if (e.isAltDown())     dy = m_deltaSlow; else
    if (e.isControlDown()) dy = m_deltaFast;

    double y = m_stage.getY();
    double h = m_stage.getHeight();
    double r = Screen.getPrimary().getVisualBounds().getHeight() - (y + h + dy);

    if (r < 0)
    {
      dy += r;

      if (dy <= 0) return;
    }

    if (e.isShiftDown())
    {
      m_stage.setHeight(h + dy);
    }

    else
    {
      m_stage.setY(y + dy);
    }
  }

  // ---------
  // onLEFTkey
  // ---------
  /**
   * @brief  This method handles the LEFT key event.
   */
  private void onLEFTkey(KeyEvent e)
  {
    double                 dx = -m_deltaDefault;
    if (e.isAltDown())     dx = -m_deltaSlow; else
    if (e.isControlDown()) dx = -m_deltaFast;

    if (e.isShiftDown())
    {
      double w = m_stage.getWidth();
      double r = w + dx;

      if (r < (m_borderFac * m_borderSize))
      {
        dx = (m_borderFac * m_borderSize) - w;

        if (dx >= 0) return;
      }

      m_stage.setWidth(w + dx);
    }

    else
    {
      double x = m_stage.getX();
      double r = x + dx;

      if (r < 0)
      {
        dx -= r;

        if (dx >= 0) return;
      }

      m_stage.setX(x + dx);
    }
  }

  // ----------
  // onRIGHTkey
  // ----------
  /**
   * @brief  This method handles the RIGHT key event.
   */
  private void onRIGHTkey(KeyEvent e)
  {
    double                 dx = m_deltaDefault;
    if (e.isAltDown())     dx = m_deltaSlow; else
    if (e.isControlDown()) dx = m_deltaFast;

    double x = m_stage.getX();
    double w = m_stage.getWidth();
    double r = Screen.getPrimary().getVisualBounds().getWidth() - (x + w + dx);

    if (r < 0)
    {
      dx += r;

      if (dx <= 0) return;
    }

    if (e.isShiftDown())
    {
      m_stage.setWidth(w + dx);
    }

    else
    {
      m_stage.setX(x + dx);
    }
  }

  // ---------
  // onPLUSkey
  // ---------
  /**
   * @brief  This method handles the '+' key event.
   */
  private void onPLUSkey(KeyEvent e)
  {
    double opc = m_stage.getOpacity() + m_opacityInc;

    if (opc > 1.0)
    {
      m_stage.setOpacity(1.0);
    }

    else
    {
      m_stage.setOpacity(opc);
    }
  }

  // ----------
  // onMINUSkey
  // ----------
  /**
   * @brief  This method handles the '-' key event.
   */
  private void onMINUSkey(KeyEvent e)
  {
    double opc = m_stage.getOpacity() - m_opacityInc;

    if (opc < m_opacityMin)
    {
      m_stage.setOpacity(m_opacityMin);
    }

    else
    {
      m_stage.setOpacity(opc);
    }
  }

  // ------------
  // onMouseWheel
  // ------------
  /**
   * @brief  This method handles mouse wheele events from the scene object.
   */
  private void onMouseWheel(ScrollEvent e)
  {
    if (e.getEventType() == ScrollEvent.SCROLL)
    {
      double opc = m_stage.getOpacity() + ((e.getDeltaY() < 0) ? -m_opacityInc : m_opacityInc);

      if (opc > 1.0)
      {
        m_stage.setOpacity(1.0);
      }

      else if (opc < m_opacityMin)
      {
        m_stage.setOpacity(m_opacityMin);
      }

      else
      {
        m_stage.setOpacity(opc);
      }
    }
  }

  // ------------------------
  // onLeftMouseButtonPressed
  // ------------------------
  /**
   * @brief  This method is called when the left mouse button is pressed on the scene.
   */
  private void onLeftMouseButtonPressed(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      m_mouseX = e.getScreenX();
      m_mouseY = e.getScreenY();
      m_stageX = m_stage.getX();
      m_stageY = m_stage.getY();
      m_stageW = m_stage.getWidth();
      m_stageH = m_stage.getHeight();
    }
  }

  // --------------
  // onNorthDragged
  // --------------
  /**
   * @brief  This method is called when the 'north' region is dragged.
   */
  private void onNorthDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dy = e.getScreenY() - m_mouseY;
      double y  = m_stageY + dy;
      double h  = m_stageH - dy;

      if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setY(y);
        m_stage.setHeight(h);
      }
    }
  }

  // ------------------
  // onNorthEastDragged
  // ------------------
  /**
   * @brief  This method is called when the 'northEast' region is dragged.
   */
  private void onNorthEastDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double dy = e.getScreenY() - m_mouseY;
      double y  = m_stageY + dy;
      double w  = m_stageW + dx;
      double h  = m_stageH - dy;

      if ((w >= m_borderFac * m_borderSize) && (h >= (m_borderFac * m_borderSize)))
      {
        m_stage.setY(y);
        m_stage.setWidth(w);
        m_stage.setHeight(h);
      }

      else if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setWidth(w);
      }

      else if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setY(y);
        m_stage.setHeight(h);
      }
    }
  }

  // -------------
  // onEastDragged
  // -------------
  /**
   * @brief  This method is called when the 'east' region is dragged.
   */
  private void onEastDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double w  = m_stageW + dx;

      if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setWidth(w);
      }
    }
  }

  // ------------------
  // onSouthEastDragged
  // ------------------
  /**
   * @brief  This method is called when the 'southEast' region is dragged.
   */
  private void onSouthEastDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double dy = e.getScreenY() - m_mouseY;
      double w  = m_stageW + dx;
      double h  = m_stageH + dy;

      if ((w >= m_borderFac * m_borderSize) && (h >= (m_borderFac * m_borderSize)))
      {
        m_stage.setWidth(w);
        m_stage.setHeight(h);
      }

      else if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setWidth(w);
      }

      else if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setHeight(h);
      }
    }
  }

  // --------------
  // onSouthDragged
  // --------------
  /**
   * @brief  This method is called when the 'south' region is dragged.
   */
  private void onSouthDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dy = e.getScreenY() - m_mouseY;
      double h  = m_stageH + dy;

      if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setHeight(h);
      }
    }
  }

  // ------------------
  // onSouthWestDragged
  // ------------------
  /**
   * @brief  This method is called when the 'southWest' region is dragged.
   */
  private void onSouthWestDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double dy = e.getScreenY() - m_mouseY;
      double x  = m_stageX + dx;
      double w  = m_stageW - dx;
      double h  = m_stageH + dy;

      if ((w >= m_borderFac * m_borderSize) && (h >= (m_borderFac * m_borderSize)))
      {
        m_stage.setX(x);
        m_stage.setWidth(w);
        m_stage.setHeight(h);
      }

      else if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setX(x);
        m_stage.setWidth(w);
      }

      else if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setHeight(h);
      }
    }
  }

  // -------------
  // onWestDragged
  // -------------
  /**
   * @brief  This method is called when the 'west' region is dragged.
   */
  private void onWestDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double x  = m_stageX + dx;
      double w  = m_stageW - dx;

      if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setX(x);
        m_stage.setWidth(w);
      }
    }
  }

  // ------------------
  // onNorthWestDragged
  // ------------------
  /**
   * @brief  This method is called when the 'northWest' region is dragged.
   */
  private void onNorthWestDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      double dx = e.getScreenX() - m_mouseX;
      double dy = e.getScreenY() - m_mouseY;
      double x  = m_stageX + dx;
      double y  = m_stageY + dy;
      double w  = m_stageW - dx;
      double h  = m_stageH - dy;

      if ((w >= (m_borderFac * m_borderSize)) && (h >= (m_borderFac * m_borderSize)))
      {
        m_stage.setX(x);
        m_stage.setY(y);
        m_stage.setWidth(w);
        m_stage.setHeight(h);
      }

      else if (w >= (m_borderFac * m_borderSize))
      {
        m_stage.setX(x);
        m_stage.setY(m_stageY + m_stageH - (m_borderFac * m_borderSize) - 1);
        m_stage.setWidth(w);
      }

      else if (h >= (m_borderFac * m_borderSize))
      {
        m_stage.setX(m_stageX + m_stageW - (m_borderFac * m_borderSize) - 1);
        m_stage.setY(y);
        m_stage.setHeight(h);
      }
    }
  }

  // ---------------
  // onCenterDragged
  // ---------------
  /**
   * @brief  This method is called when the 'center' region is dragged.
   */
  private void onCenterDragged(MouseEvent e)
  {
    if (e.isPrimaryButtonDown())
    {
      m_stage.setX( m_stageX + (e.getScreenX() - m_mouseX) );
      m_stage.setY( m_stageY + (e.getScreenY() - m_mouseY) );
    }
  }

  // --------------------
  // onNorthDoubleClicked
  // --------------------
  /**
   * @brief  This method is called when the 'north' region is double clicked.
   */
  private void onNorthDoubleClicked(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (e.getClickCount() == 2)
      {
        if (m_northReturn)
        {
          m_northReturn = false;

          m_stage.setX(m_northReturnX);
          m_stage.setY(m_northReturnY);
          m_stage.setWidth(m_northReturnW);
          m_stage.setHeight(m_northReturnH);
          m_stage.setOpacity(m_northReturnO);
        }

        else
        {
          m_northReturn  = true;
          m_northReturnX = m_stage.getX();
          m_northReturnY = m_stage.getY();
          m_northReturnW = m_stage.getWidth();
          m_northReturnH = m_stage.getHeight();
          m_northReturnO = m_stage.getOpacity();

          m_stage.setX(0);
          m_stage.setX(0);
          m_stage.setWidth(Screen.getPrimary().getVisualBounds().getWidth());
          m_stage.setHeight(Screen.getPrimary().getVisualBounds().getHeight());
          m_stage.setOpacity(1.0);
        }
      }
    }
  }

  // ------------------------
  // onNorthEastDoubleClicked
  // ------------------------
  /**
   * @brief  This method is called when the 'northEast' region is double clicked.
   */
  private void onNorthEastDoubleClicked(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (e.getClickCount() == 2)
      {
        Platform.exit();
        System.exit(0);
      }
    }
  }

  // -------------------
  // onEastDoubleClicked
  // -------------------
  /**
   * @brief  This method is called when the 'east' region is double clicked.
   */
  private void onEastDoubleClicked(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (e.getClickCount() == 2)
      {
        m_stage.setY( (Screen.getPrimary().getBounds().getHeight() - m_stage.getHeight()) / 2 );
      }
    }
  }

  // --------------------
  // onSouthDoubleClicked
  // --------------------
  /**
   * @brief  This method is called when the 'south' region is double clicked.
   */
  private void onSouthDoubleClicked(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (e.getClickCount() == 2)
      {
        m_stage.setX( (Screen.getPrimary().getBounds().getWidth() - m_stage.getWidth()) / 2 );
      }
    }
  }

  // ---------------------
  // onCenterDoubleClicked
  // ---------------------
  /**
   * @brief  This method is called when the 'center' region is double clicked.
   */
  private void onCenterDoubleClicked(MouseEvent e)
  {
    if (e.getButton().equals(MouseButton.PRIMARY))
    {
      if (e.getClickCount() == 2)
      {
        onSendMessage();
      }
    }
  }


  // ---------------------------------------------------------------------------
  // Attributes                                                       Attributes
  // ---------------------------------------------------------------------------

  private final int    m_initialWidth;
  private final int    m_initialHeight;
  private final double m_initialOpacity;
  private final int    m_borderFac;
  private final int    m_borderSize;
  private final double m_opacityMin;
  private final double m_opacityInc;
  private final double m_deltaSlow;
  private final double m_deltaDefault;
  private final double m_deltaFast;
  private final int    m_delayPre;
  private final int    m_delayPost;
  private double       m_mouseX;
  private double       m_mouseY;
  private double       m_stageX;
  private double       m_stageY;
  private double       m_stageW;
  private double       m_stageH;
  private boolean      m_northReturn;
  private double       m_northReturnX;
  private double       m_northReturnY;
  private double       m_northReturnW;
  private double       m_northReturnH;
  private double       m_northReturnO;
  private Stage        m_stage;
  private Background   m_corner;
  private Background   m_edge;
  private Region       m_north;
  private Region       m_northEast;
  private Region       m_east;
  private Region       m_southEast;
  private Region       m_south;
  private Region       m_southWest;
  private Region       m_west;
  private Region       m_northWest;
  private Region       m_center;

}


// -------------
// WaitPrintShow
// -------------
/**
 * @brief  This thread waits some milli seconds before and after
 *         pushing some text to stdout.
 */
class WaitPrintShow extends Thread
{

  // -------------
  // WaitPrintShow
  // -------------
  /**
   * @brief  This constructor initializes all attributes.
   */
  public WaitPrintShow(int preMillis, int postMillis, Stage stage, double opacity, int x, int y, int w, int h)
  {
    m_preMillis  = preMillis;
    m_postMillis = postMillis;
    m_stage      = stage;
    m_opacity    = opacity;
    m_x          = x;
    m_y          = y;
    m_w          = w;
    m_h          = h;
  }

  // ---
  // run
  // ---
  /**
   * @brief  The thread's commands.
   */
  public void run()
  {
    // wait
    try { sleep(m_preMillis); } catch (Exception any) { /* nothing */ }

    // print message
    System.out.println(String.format("x=%d;y=%d;w=%d;h=%d;", m_x, m_y, m_w, m_h));

    // wait
    try { sleep(m_postMillis); } catch (Exception any) { /* nothing */ }

    // show stage
    m_stage.setOpacity(m_opacity);
  }


  // ---------------------------------------------------------------------------
  // Attributes                                                       Attributes
  // ---------------------------------------------------------------------------

  private int    m_preMillis;
  private int    m_postMillis;
  private Stage  m_stage;
  private double m_opacity;
  private int    m_x;
  private int    m_y;
  private int    m_w;
  private int    m_h;

}

