/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
#            (C) 2015 Hiroshi Miura
#
#  This program is free software; you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation; either version 2 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program; if not, write to the Free Software
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
#
#######################################################################
*/


package bitext2tmx.ui;

import bitext2tmx.util.gui.AquaAdapter;
import bitext2tmx.ui.dialogs.About;
import bitext2tmx.ui.dialogs.FontSelector;
import bitext2tmx.ui.dialogs.Encodings;
import bitext2tmx.ui.help.Manual;
import bitext2tmx.ui.dialogs.OpenTexts;
import bitext2tmx.core.Document;
import bitext2tmx.core.TMXWriter;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.*;

import com.vlsolutions.swing.docking.*;
import com.vlsolutions.swing.docking.ui.DockingUISettings;

import static org.openide.awt.Mnemonics.setLocalizedText;

import bitext2tmx.core.Align;
import bitext2tmx.core.TMXReader;
import bitext2tmx.engine.Segment;
import bitext2tmx.engine.SegmentChanges;
import bitext2tmx.util.AppConstants;

import bitext2tmx.util.RuntimePreferences;
import bitext2tmx.util.Utilities;

import static bitext2tmx.util.StringUtil.formatText;
import static bitext2tmx.util.StringUtil.restoreText;
import static bitext2tmx.util.gui.AquaAdapter.*;
import static bitext2tmx.util.Localization.*;
import static bitext2tmx.util.Utilities.*;


/**
*
*/
final public class MainWindow extends JFrame implements ActionListener,
  WindowListener
{
  final private static long serialVersionUID = -540065960678391862L;

  private DockingDesktop  _Desktop;

  final private AlignmentsView  _vwAlignments   = new AlignmentsView( this );
  final private SegmentEditor   _edLeftSegment  = new SegmentEditor( this );
  final private SegmentEditor   _edRightSegment = new SegmentEditor( this );
  final private ControlView    _vwControls     = new ControlView( this );

  //  Menubar
  final private JMenuBar  _mbar = new JMenuBar();

  //  File menu
  private JMenu      _mnuFile;
  private JMenuItem  _mniFileOpen;
  private JMenuItem  _mniFileSave;
  private JMenuItem  _mniFileSaveAs;
  private JMenuItem  _mniFileClose;
  private JMenuItem  _mniFileQuit;

  //  Settings menu
  private JMenu      _mnuSettings;
  private JMenuItem  _mniSettingsFonts;
  private JMenuItem  _mncbSettingsLinebreak;

  //  Look and Feel submenu
  private JMenu      _mnuLaf;
  private JMenuItem  _mniLafGtk;
  private JMenuItem  _mniLafLiquid;
  private JMenuItem  _mniLafMetal;
  private JMenuItem  _mniLafNimbus;
  private JMenuItem  _mniLafSystem;

  //  Help menu
  private JMenu      _mnuHelp;
  private JMenuItem  _mniHelpManual;
  private JMenuItem  _mniHelpAbout;

  //  Statusbar
  private JPanel  _pnlStatusBar;
  private JLabel  _lblStatusBar;

  private Document  _alstOriginal;
  private Document  _alstTranslation;

  final private ArrayList  _alstBitext;

  final private ArrayList<SegmentChanges>  _alstChanges;
  final private ArrayList  _alstLang;


  private int  _topArrays;    //  =  0;
  private int  _posTextArea;  //  =  0;
  private int  _iChanges     = -1;
  private int  _iIdentLabel;  //  =  0;
  private int  _identAnt;     //  =  0;

  private String  _strLangOriginal    = "en";
  private String  _strLangTranslation = "en";
  private String  _strOriginal;
  private String  _strTranslation;

  private File  _fUserHome   = new File( System.getProperty( "user.home" ) );
  private File  _fPathOriginal;
  private File  _fPathTranslation;

  private String  _ult_recorridoinv;

  private Font  _fntUserInterface;
  private Font  _fntTableHeader;
  private Font  _fntTable;
  private Font  _fntSourceEditor;
  private Font  _fntTargetEditor;


  public MainWindow()
  {
    this._alstBitext = new ArrayList();
    this._alstChanges = new ArrayList<>();
    this._alstLang = new ArrayList();
    
    initDockingUI();
    makeUI();
    setWindowIcon();

    //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
    try
    {
      AquaAdapter.connect( this, "displayAbout", AquaEvent.ABOUT );
      AquaAdapter.connect( this, "quit", AquaEvent.QUIT );
    }
    catch( final NoClassDefFoundError e )
    {
     System.out.println( e );
    }

    setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );
    addWindowListener( new WindowAdapter()
      {@Override
       final public void windowClosing( final WindowEvent e ) {
         quit(); 
       } } );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize = this.getSize();

    if( frameSize.height > screenSize.height ) frameSize.height = screenSize.height;
    if( frameSize.width > screenSize.width ) frameSize.width = screenSize.width;

    this.setLocation( ( screenSize.width - frameSize.width ) / 2,
      ( screenSize.height - frameSize.height ) / 2 );

    setFonts( null );

  }
  private static final Logger LOG = Logger.getLogger(MainWindow.class.getName());


  private void initDockingUI()
  {
    DockingUISettings.getInstance().installUI();

    UIManager.put( "DockViewTitleBar.titleFont", getUserInterfaceFont() );

    UIManager.put( "DockViewTitleBar.close",          getDesktopIcon( "close.png" ) );
    UIManager.put( "DockViewTitleBar.close.rollover", getDesktopIcon( "close_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.close.pressed",  getDesktopIcon( "close_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.hide",              getDesktopIcon( "min.png" ) );
    UIManager.put( "DockViewTitleBar.hide.rollover",     getDesktopIcon( "min_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.hide.pressed",      getDesktopIcon( "min_pressed.png" ) );
    UIManager.put( "DockViewTitleBar.maximize",          getDesktopIcon( "max.png" ) );
    UIManager.put( "DockViewTitleBar.maximize.rollover", getDesktopIcon( "max_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.maximize.pressed", getDesktopIcon( "max_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.restore",          getDesktopIcon( "restore.png" ) );
    UIManager.put( "DockViewTitleBar.restore.rollover", getDesktopIcon( "restore_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.restore.pressed", getDesktopIcon( "restore_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.dock",          getDesktopIcon( "restore.png" ) );
    UIManager.put( "DockViewTitleBar.dock.rollover", getDesktopIcon( "restore_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.dock.pressed",  getDesktopIcon( "restore_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.float",          getDesktopIcon( "shade.png" ) );
    UIManager.put( "DockViewTitleBar.float.rollover", getDesktopIcon( "shade_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.float.pressed",  getDesktopIcon( "shade_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.attach",          getDesktopIcon( "un_shade.png" ) );
    UIManager.put( "DockViewTitleBar.attach.rollover", getDesktopIcon( "un_shade_hovered.png" ) );
    UIManager.put( "DockViewTitleBar.attach.pressed",  getDesktopIcon( "un_shade_pressed.png" ) );

    UIManager.put( "DockViewTitleBar.menu.hide",     getDesktopIcon( "min.png" ) );
    UIManager.put( "DockViewTitleBar.menu.maximize", getDesktopIcon( "max.png" ) );
    UIManager.put( "DockViewTitleBar.menu.restore",  getDesktopIcon( "restore.png" ) );
    UIManager.put( "DockViewTitleBar.menu.dock",     getDesktopIcon( "restore.png" ) );
    UIManager.put( "DockViewTitleBar.menu.float",    getDesktopIcon( "shade.png" ) );
    UIManager.put( "DockViewTitleBar.menu.attach",   getDesktopIcon( "un_shade.png" ) );
    UIManager.put( "DockViewTitleBar.menu.close",    getDesktopIcon( "close.png" ) );

    UIManager.put( "DockTabbedPane.close",           getDesktopIcon( "close.png" ) );
    UIManager.put( "DockTabbedPane.close.rollover",  getDesktopIcon( "close_hovered.png" ) );
    UIManager.put( "DockTabbedPane.close.pressed",   getDesktopIcon( "close_pressed.png" ) );

    UIManager.put( "DockTabbedPane.menu.close",         getDesktopIcon( "close.png" ) );
    UIManager.put( "DockTabbedPane.menu.hide",          getDesktopIcon( "shade.png" ) );
    UIManager.put( "DockTabbedPane.menu.maximize",      getDesktopIcon( "max.png" ) );
    UIManager.put( "DockTabbedPane.menu.float",         getDesktopIcon( "shade.png" ) );
    UIManager.put( "DockTabbedPane.menu.closeAll",      getDesktopIcon( "close.png" ) );
    UIManager.put( "DockTabbedPane.menu.closeAllOther", getDesktopIcon( "close.png" ) );

    UIManager.put( "DragControler.detachCursor", getDesktopIcon( "shade.png" ).getImage() );


    UIManager.put( "DockViewTitleBar.closeButtonText",    getString( "VW.TITLEBAR.BTNCLOSE" ) );
    UIManager.put( "DockViewTitleBar.minimizeButtonText", getString( "VW.TITLEBAR.BTNMINIMIZE" ) );
    UIManager.put( "DockViewTitleBar.maximizeButtonText", getString( "VW.TITLEBAR.BTNMAXIMIZE" ) );
    UIManager.put( "DockViewTitleBar.restoreButtonText",  getString( "VW.TITLEBAR.BTNRESTORE" ) );
    UIManager.put( "DockViewTitleBar.floatButtonText",    getString( "VW.TITLEBAR.BTNFLOAT" ) );
    UIManager.put( "DockViewTitleBar.attachButtonText",   getString( "VW.TITLEBAR.BTNATTACH" ) );

    UIManager.put( "DockTabbedPane.closeButtonText",    getString( "TAB.BTNCLOSE" ) );
    UIManager.put( "DockTabbedPane.minimizeButtonText", getString( "TAB.BTNMINIMIZE" ) );
    UIManager.put( "DockTabbedPane.restoreButtonText",  getString( "TAB.BTNRESTORE" ) );
    UIManager.put( "DockTabbedPane.maximizeButtonText", getString( "TAB.BTNMAXIMIZE" ) );
    UIManager.put( "DockTabbedPane.floatButtonText",    getString( "TAB.BTNFLOAT" ) );

  }

  private ImageIcon getDesktopIcon( final String iconName )
  {
    if( isMacOSX() )
      return( getIcon( "desktop/osx/" + iconName ) );

    return( getIcon( "desktop/" + iconName) );
  }

  private ImageIcon getIcon( final String iconName )
  { return Icons.getIcon( iconName ); }

  /**  Set root window icon */
  private void setWindowIcon()
  {
    try{ 
      //setIconImage( Icons.getIcon( "icon-small.png" ).getImage() ); 
    }
    catch( final Exception e )
    { System.out.println( "Error loading icon: " + e ); }
  }

  private void makeUI()
  {
    makeMenus();

    _lblStatusBar = new JLabel( " " );
    _pnlStatusBar = new JPanel();
    _pnlStatusBar.setLayout( new BoxLayout( _pnlStatusBar, BoxLayout.LINE_AXIS ) );
    _pnlStatusBar.add( Box.createRigidArea( new Dimension( 10, 0 ) ) );
    _pnlStatusBar.add( _lblStatusBar );

    _Desktop = new DockingDesktop();
    getContentPane().add( _Desktop, BorderLayout.CENTER );

    //  dock objects already exist at this point
    _Desktop.registerDockable( _vwAlignments );
    _Desktop.registerDockable( _edLeftSegment );
    _Desktop.registerDockable( _edRightSegment );
    _Desktop.registerDockable( _vwControls );

    DockKey keyAlignmentTable = _vwAlignments    .getDockKey();
    DockKey keyLeftSegment    = _edLeftSegment   .getDockKey();
    DockKey keyRightSegment   = _edRightSegment  .getDockKey();
    DockKey keySegmentButtons = _vwControls      .getDockKey();

    keyLeftSegment.setName( getString( "VW.ORIGINAL.NAME" ) );
    keyLeftSegment.setTooltip( getString( "VW.ORIGINAL.TOOLTIP" ) );
    keyRightSegment.setName( getString( "VW.TRANSLATION.NAME" ) );
    keyRightSegment.setTooltip( getString( "VW.TRANSLATION.TOOLTIP" ) );

    keyAlignmentTable  .setAutoHideBorder( DockingConstants.HIDE_BOTTOM );
    keyLeftSegment     .setAutoHideBorder( DockingConstants.HIDE_BOTTOM );
    keyRightSegment    .setAutoHideBorder( DockingConstants.HIDE_BOTTOM );
    keySegmentButtons  .setAutoHideBorder( DockingConstants.HIDE_BOTTOM );

    keyAlignmentTable  .setFloatEnabled( true );
    keyLeftSegment     .setFloatEnabled( true );
    keyRightSegment    .setFloatEnabled( true );
    keySegmentButtons  .setFloatEnabled( true );

    keyAlignmentTable  .setCloseEnabled( false );
    keyLeftSegment     .setCloseEnabled( false );
    keyRightSegment    .setCloseEnabled( false );
    keySegmentButtons  .setCloseEnabled( false );

    keySegmentButtons  .setResizeWeight( 0.1f );

    _Desktop.addDockable( _vwAlignments );

    _Desktop.split( _vwAlignments, _edLeftSegment, DockingConstants.SPLIT_BOTTOM );
    _Desktop.split( _edLeftSegment, _vwControls, DockingConstants.SPLIT_BOTTOM );
    _Desktop.split( _edLeftSegment, _edRightSegment, DockingConstants.SPLIT_RIGHT );

    this.setSize( new Dimension( 800, 600 ) );
    this.setMinimumSize( new Dimension( 640, 480 ) );

    setTitle(AppConstants.getDisplayNameAndVersion() );
    getContentPane().add( _pnlStatusBar, BorderLayout.SOUTH );
  }

  /** Used by makeMenuComponent to select componenet type */
  public static enum MenuComponentType { CHECKBOX, ITEM, MENU, RADIOBUTTON };

  /**
   *  Return a new menu component
   *
   *  Can return subclasses of JMenuItem: including JMenu!
   *  Downcast return type to as needed
   */
  private <T extends JMenuItem> T makeMenuComponent( final MenuComponentType mniType,
    final KeyStroke ksShortcut, final ImageIcon icon, final String strText,
      final String strKey )
  {
    JMenuItem mni = null;

    switch( mniType )
    {
      case ITEM:        mni = new JMenuItem();            break;
      case CHECKBOX:    mni = new JCheckBoxMenuItem();    break;
      case MENU:        mni = new JMenu();                break;
      case RADIOBUTTON: mni = new JRadioButtonMenuItem(); break;
    }

    //  Default text, when no localization available, testing
    mni.setText( strText );

    if( ksShortcut != null )  mni.setAccelerator( ksShortcut );
    if( !isMacOSX() && icon != null ) mni.setIcon( icon );

    mni.addActionListener( this );

    //  Localized text
    if( strKey != null ) setLocalizedText( mni, getString( strKey ) );

    @SuppressWarnings("unchecked")
    T res = (T)mni;
    return( res );
  }

  private void makeMenus()
  {
    _mnuFile = makeMenuComponent( MenuComponentType.MENU, null, null, "File", "MNU.FILE" );

    _mniFileOpen = makeMenuComponent( MenuComponentType.ITEM, KeyStroke.
      getKeyStroke( 'O', KeyEvent.CTRL_MASK, false ), getIcon( "project_open.png" ),
        "Open...", "MNI.FILE.OPEN" );

    _mniFileSave = makeMenuComponent( MenuComponentType.ITEM,
      KeyStroke.getKeyStroke( 'S', KeyEvent.CTRL_MASK, false ),
        getIcon( "filesave.png" ), "Save", "MNI.FILE.SAVE" );
    _mniFileSave.setEnabled( false );

    _mniFileSaveAs = makeMenuComponent( MenuComponentType.ITEM, null ,
      getIcon( "filesave.png" ), "Save As...", "MNI.FILE.SAVEAS" );
    _mniFileSaveAs.setEnabled( false );

    _mniFileClose = makeMenuComponent( MenuComponentType.ITEM,
      KeyStroke.getKeyStroke( 'W', KeyEvent.CTRL_MASK, false ),
        getIcon( "fileclose.png" ), "Close", "MNI.FILE.ABORT" );
    _mniFileClose.setEnabled( false );

    _mniFileQuit = makeMenuComponent( MenuComponentType.ITEM,
      KeyStroke.getKeyStroke( 'Q', KeyEvent.CTRL_MASK, false ),
        getIcon( "application-exit.png" ), "Quit", "MNI.FILE.EXIT" );

    _mnuSettings = makeMenuComponent( MenuComponentType.MENU, null, null,
      "Settings", "MNU.SETTINGS" );

    _mncbSettingsLinebreak = makeMenuComponent( MenuComponentType.CHECKBOX, null, null,
      "Linebreaks", "MNI.SETTINGS.LINEBREAK" );
    _mncbSettingsLinebreak.setToolTipText( getString( "MNI.SETTINGS.LINEBREAK.TOOLTIP" ) );
    _mncbSettingsLinebreak.addChangeListener( new javax.swing.event.ChangeListener()
      { final public void stateChanged( final ChangeEvent e )
        { 
          RuntimePreferences.setSegmentByLineBreak(_mncbSettingsLinebreak.isSelected());
          onLinebreakToggle();
        }
      } );

    _mniSettingsFonts = makeMenuComponent( MenuComponentType.ITEM, null,
      getIcon( "fonts.png" ), "Configure Fonts...", "MNI.SETTINGS.FONTS" );
    _mniSettingsFonts.setToolTipText( getString( "MNI.SETTINGS.FONTS.TOOLTIP" ) );

    _mnuHelp = makeMenuComponent( MenuComponentType.MENU, null, null,
      "Help", "MNU.HELP" );

    _mniHelpAbout = makeMenuComponent( MenuComponentType.ITEM, null,
      getIcon( "icon-small.png" ), "About", "MNI.HELP.ABOUT" );

    _mniHelpManual = makeMenuComponent( MenuComponentType.ITEM, 
      KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ),
        getIcon( "help-contents.png" ), "Manual", "MNI.HELP.MANUAL" );

    _mnuFile.add( _mniFileOpen );
    _mnuFile.addSeparator();
    _mnuFile.add( _mniFileSave );
    _mnuFile.add( _mniFileSaveAs );
    _mnuFile.addSeparator();
    _mnuFile.add( _mniFileClose );

    if( !isMacOSX() )
    {
      _mnuFile.addSeparator();
      _mnuFile.add( _mniFileQuit );
    }

    _mnuSettings.add( _mncbSettingsLinebreak );
    _mnuSettings.add( _mniSettingsFonts );

    if( !isMacOSX() )
    {
      _mnuLaf = makeMenuComponent( MenuComponentType.MENU, null, null,
        "Look and Feel", null );

      _mniLafLiquid = makeMenuComponent( MenuComponentType.ITEM, null, null, "Liquid",
        null );

      _mniLafMetal = makeMenuComponent( MenuComponentType.ITEM, null, null, "Metal",
        null );

      _mniLafNimbus = makeMenuComponent( MenuComponentType.ITEM, null, null, "Nimbus",
        null );

      _mniLafSystem = makeMenuComponent( MenuComponentType.ITEM, null, null, "System",
        null );

      _mniLafLiquid  .setMnemonic( 'L' );
      _mniLafMetal   .setMnemonic( 'M' );
      _mniLafNimbus  .setMnemonic( 'N' );
      _mniLafSystem  .setMnemonic( 'Y' );

      if( !isWindows() )
      {
        _mniLafGtk = makeMenuComponent( MenuComponentType.ITEM, null, null, "Gtk",
          null );
        _mniLafGtk.setMnemonic( 'G' );
        _mnuLaf.add( _mniLafGtk );
      }

      _mnuLaf.add( _mniLafLiquid );
      _mnuLaf.add( _mniLafMetal );
      _mnuLaf.add( _mniLafNimbus );
      _mnuLaf.add( _mniLafSystem );

      _mnuSettings.add( _mnuLaf );
    }

    _mnuSettings.add( _mnuLaf );

    _mnuHelp.add( _mniHelpManual );

    if( !isMacOSX() )
    {
      _mnuHelp.addSeparator();
      _mnuHelp.add( _mniHelpAbout );
    }

    _mbar.add( _mnuFile );
    _mbar.add( _mnuSettings );
    _mbar.add( _mnuHelp );

    setJMenuBar( _mbar );
  }

  /**
   *  Quit application
   *
   *  @return boolean - OS X Aqua integration only
   */
  private boolean quit()
  {
    //  ToDo: check for unsaved changes first, save, then quit

    SwingUtilities.invokeLater( new Runnable()
      { public void run() { dispose(); } } );

    return( true );
  }

  /**
   *  Open dialog to select the files we want to align/convert
   *
   *  Esta funci�n abre un cuadro de di�logo para seleccionar los ficheros que
   *  queremos alinear.
   */
  private void onOpen()
  {
    String originalEncoding;
    String translateEncoding;

    final OpenTexts dlg = new OpenTexts();
    dlg.setPath( _fUserHome );
    dlg.setModal( true );
    dlg.setVisible( true );

    if( !dlg.isClosed() )
    {
      _fUserHome = dlg.getPath();
      originalEncoding = (String)dlg.getSourceLangEncComboBox().getSelectedItem();
      _fPathOriginal   = dlg.getSourcePath();
      _strOriginal     = dlg.getSource();
      _strLangOriginal = dlg.getSourceLocale();
      _vwAlignments.buildDisplay();
      _alstOriginal = new Document();
      _alstTranslation = new Document();
        
      if( dlg.getTypes() == 0 )
      {
        //getSelectedItem()
        translateEncoding  = (String)dlg.getTargetLangEncComboBox().getSelectedItem();
        _fPathTranslation   = dlg.getTargetPath();
        _strTranslation     = dlg.getTarget();
        _strLangTranslation = dlg.getTargetLocale();

        try {
          _alstOriginal.readDocument(_strOriginal, originalEncoding);
          _alstTranslation.readDocument(_strTranslation, translateEncoding);
        } catch (Exception ex) {
          JOptionPane.showMessageDialog( this, getString( "MSG.ERROR" ),
             getString( "MSG.ERROR.FILE.READ" ), JOptionPane.ERROR_MESSAGE );
          this.dispose();
        }
        
        boolean res = Align.align(_alstOriginal, _alstTranslation);

        if( res )
        {
          matchArrays();

          for( int cont=0; cont < _alstOriginal.size(); cont++ )
          {
            if( _alstOriginal.get( cont ) == null ||
                (_alstOriginal.get( cont ).equals( "" ) ) &&
                  ( _alstTranslation.get( cont ) == null ||
                    _alstTranslation.get( cont ).equals( "" ) ) )
            {
              _alstOriginal.remove( cont );
              _alstTranslation.remove( cont );
            }
          }
        }
      }
      else
      {
        try {
          TMXReader.readTMX(_fPathOriginal, originalEncoding,
                            _strLangOriginal, _strLangTranslation,
                            _alstOriginal, _alstTranslation);
        } catch (Exception ex) {
          Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        _fPathTranslation = _fPathOriginal;
      }

      initializeAlignmentsView();
      updateAlignmentsView();
      _vwControls.enableButtons( true );
      //_vwControls._btnUndo.setEnabled( false );
      _vwControls.setUndoEnabled( false );
      _mniFileSaveAs.setEnabled( true );
      _mniFileClose.setEnabled( true );

      dlg.dispose();
    }
  }


  /**
   *  Actions to perform when closing alignment/editing session.
   *  Leaves the text as it was so that it can be processed later.
   *
   *  Esta funci�n contiene la funcionalidad del bot�n abortar.
   *  Deja el texto como estaba para volver a tratarlo m�s tarde.
   */
  private void onClose()
  {
    clear();
    //BotonesFalse();
    _vwControls.enableButtons( false );
    //  Done in _vwControls
    //_btnUndo.setEnabled( false );
    // Undo is separate
    _vwControls.setUndoEnabled( false );
    _mniFileSave.setEnabled( false );
    _mniFileSaveAs.setEnabled( false );
    _mniFileClose.setEnabled( false );
  }

  //  ToDo: implement proper functionality; not used currently
  final void onSave()   { saveBitext(); }

  final void onSaveAs() { saveBitext(); }

  final void displayAbout()
  {
    final About dlg = new About( this );
    dlg.setVisible( true );
  }


  /**
   *  Necessary capabilities to store the bitext
   *
   */
  private void saveBitext()
  {
    File userHome;
    
    for( int cont = 0; cont < ( _alstOriginal.size() - 1 ); cont++ )
    {
      if( _alstOriginal.get( cont ).equals( "" ) && _alstTranslation.get( cont ).equals( "" ) )
      {
        _alstOriginal.remove( cont );
        _alstTranslation.remove( cont );
      }
    }
    
    try {
      final String nombre = _fPathOriginal.getName().
          substring( 0, ( _fPathOriginal.getName().length() - 4 ) );
      boolean guardar = false;
      boolean salir   = false;
    
      userHome = new File(RuntimePreferences.getUserHome());
      File fNombre = new File( nombre.concat( _strLangTranslation + ".tmx" ) );
      
      while( !guardar && !salir )
      {
        final JFileChooser fc = new JFileChooser();

        //  switch() on language removed from here -RM

        boolean kNombre_Usuario = false;

        while( !kNombre_Usuario )
        {
          fc.setLocation( 230, 300 );
          fc.setCurrentDirectory( userHome );
          fc.setDialogTitle( getString( "DLG.SAVEAS" ) );

          fc.setMultiSelectionEnabled( false );
          fc.setSelectedFile( fNombre );
          fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
          userHome = fc.getCurrentDirectory();

          int returnVal;
          returnVal = fc.showSaveDialog( this );

          if( returnVal == JFileChooser.APPROVE_OPTION )
          {
            returnVal = 1;
            fNombre = fc.getSelectedFile();

            if( !fNombre.getName().endsWith( ".tmx" ) )
              fNombre.getName().concat( ".tmx" );

            kNombre_Usuario = true;
          }
          else
          {
            kNombre_Usuario = true;
            salir = true;
          }
        }

        int n;
        if( kNombre_Usuario && !salir )
        {
          //  Comprobar si ya existe el fichero
          //  Check whether the file exists
          if( fNombre.exists() )
          {
            // "Guardar"/"Save"  & "Cancelar"/"Cancel"
            final Object[] options = { getString( "BTN.SAVE" ),
              getString( "BTN.CANCEL" ) };

            n = JOptionPane.showOptionDialog( this, getString( "MSG.FILE.EXISTS" ), getString( "MSG.WARNING" ),
                 JOptionPane.OK_CANCEL_OPTION,   JOptionPane.QUESTION_MESSAGE,
                   null, options, options[0] );

            //  Sobreescribir - Overwrite
            if( n == 0 ) guardar = true;
          }
          // No existe->guardar
          // Doesn't exist ->save
          else guardar = true;

        }
      }

      String encoding = "UTF-8";
      if( guardar )
      {
        //  Pedir el encoding
        //  Ask for the encoding
        Encodings dlgEnc = new Encodings();

        dlgEnc.setVisible( true );

        if( !dlgEnc.isClosed() )
        {
        encoding = dlgEnc.getComboBoxEncoding();
        dlgEnc.dispose();
        }
      }
      
      TMXWriter.writeBitext(fNombre,
              _alstOriginal, _strLangOriginal,
              _alstTranslation, _strLangTranslation,
              encoding);
      
    } catch ( IOException ex) {
      JOptionPane.
        showMessageDialog( this, (String)_alstLang.get( 21 ),
          (String)_alstLang.get( 18 ), JOptionPane.ERROR_MESSAGE );
      this.dispose();
    }
  }


  /**
   *  Esta funci�n inicializa los valores para comenzar la validaci�n
   *  del siguiente alineamiento.
   *
   *  Initialize values to start the validation of the following alignment
   */
  private void clear()
  {
    int cont = _alstOriginal.size() - 1;

    while( !_alstOriginal.isEmpty() ) _alstOriginal.remove( cont-- );

    cont = _alstTranslation.size() - 1;

    while( !_alstTranslation.isEmpty() ) _alstTranslation.remove( cont-- );

    cont = _alstBitext.size() - 1;

    while( !_alstBitext.isEmpty() ) _alstBitext.remove( cont-- );

    cont = _alstChanges.size() - 1;

    while( !_alstChanges.isEmpty() ) _alstChanges.remove( cont-- );

    _iChanges    = -1;
    _iIdentLabel = 0;
    _identAnt      = 0;
    _fPathTranslation   = null;
    _fPathOriginal = null;
    //_tbl.setRowSelectionInterval( 0, 0 );
    //_vwAlignments.setRowSelectionInterval( 0, 0 );
    //_vwControls._btnUndo.setEnabled( false );
    _vwControls.setUndoEnabled( false );
    //fichCreado = false;

  //System.out.println( "_topArrays: " + _topArrays );
  //System.out.println( "Model RowCount: " + _vwAlignments._model.getRowCount() );
    //for( int i = _topArrays; i >= 25; i-- ) _model.removeSegment( i );

    _vwAlignments.clear();
    _topArrays = 0;

    _edLeftSegment.setText( "" );
    _edRightSegment.setText( "" );
  }


  /**
   *  Esta funci�n actualiza los cambios a�adiendo un cambio de
   *  unir en el array de deshacer y aplica la uni�n
   *
   *  @param textAreaIzq
   *    :TRUE si hay que unir la parte izq. (El texto fuente)
   *
   *  Updates the changes adding a "join" change in the "undo" array
   *  and performs the "join". (not sure about the translation)
   *
   *  @param textAreaIzq
   *   :TRUE si hay que unir la parte izq. (El texto fuente)
   *   :TRUE if the left text (source text) has to be joined
   */
  private void join( final boolean textAreaIzq )
  {
    if( _iIdentLabel != _topArrays )
    {
      final SegmentChanges Changes = new SegmentChanges( 0, _posTextArea, textAreaIzq,
        "", _iIdentLabel );
      _alstChanges.add( _iChanges, Changes );

      if( textAreaIzq ) Changes.setFrase( _alstOriginal.get( _iIdentLabel ).toString() );
      else Changes.setFrase( _alstTranslation.get( _iIdentLabel ).toString() );

      modifyAlignments( textAreaIzq, 0, 0 );
    }
  }


  /**
   * Funci�n Borrar. Esta funci�n actualiza los cambios a�adiendo un cambio de
   * borrar en el array de deshacer y aplica el borrado.
   *
   * @param textAreaIzq
   *        :TRUE si hay que borrar la parte izq. (El texto fuente)
   *
   * Function Borrar. This function updates the changes adding a delete change 
   * to the undo array and deletes
   *
   * @param textAreaIzq
   *        :TRUE if the left hand (source text) has to be deleted
   */
  private void delete( final boolean textAreaIzq )
  {
    final SegmentChanges Changes = new SegmentChanges( 1, _posTextArea, textAreaIzq,
      "", _iIdentLabel );
    _alstChanges.add( _iChanges, Changes );

    if( textAreaIzq ) Changes.setFrase(_alstOriginal.get( _iIdentLabel ) );
    else Changes.setFrase(_alstTranslation.get( _iIdentLabel ) );

    modifyAlignments( textAreaIzq, 1, 0 );
  }

  /**
   * Funci�n Split. Esta funci�n actualiza los cambios a�adiendo un cambio de
   * separar en el array de deshacer y aplica la separaci�n de la frase.
   *
   * @param textAreaIzq
   *        :TRUE si hay que separar la parte izq. (El texto fuente)
   *
   * Funci�n Split. This function updates the changes adding a split to the 
   * undo array and performs the splitting
   *
   * @param textAreaIzq
   *        :TRUE if the left hand (source text) has to be split
   */
  private void split( final boolean textAreaIzq )
  {
    if( textAreaIzq )
    {
      if( _posTextArea >= _alstOriginal.get( _iIdentLabel ).length() )
        _posTextArea = 0;
    }
    else
      if( _posTextArea >= _alstTranslation.get( _iIdentLabel ).length() )
        _posTextArea = 0;

    final SegmentChanges Changes = new SegmentChanges( 2, _posTextArea, textAreaIzq, "",
        _iIdentLabel );
    _alstChanges.add( _iChanges, Changes );

    if( textAreaIzq ) Changes.setFrase( _alstOriginal.get( _iIdentLabel ).toString() );
    else Changes.setFrase(_alstTranslation.get( _iIdentLabel ) );

    modifyAlignments( textAreaIzq, 2, Changes.getPosition() );
  }


  /**
   *  Funci�n ModLista. Esta funci�n realiza los cambios: Unir: une la fila
   *  seleccionada con la siguiente. Borrar: borra la fila seleccionada. Split:
   *  separa la fila seleccionada por la posici�n indicada creando dos filas.
   *
   *  @param izq:        TRUE si se aplica al texto fuente
   *  @param operacion:  0->Unir,1->Borrar,2->Split
   *  @param posicion:   por donde hay que hacer el split
   *
   * Function ModLista. This function performes the following changes. Join: 
   *  joins the selected row with the following; Delete: deletes the selected
   *  row; Split: splits the selected row at the given position creating two
   *  rows.
   *
   *  @param izq:        TRUE if it applies to the source text
   *  @param operacion:  0->Join,1->Delete,2->Split
   *  @param posicion:   position at which the split is performed
   */
  private void modifyAlignments( final boolean izq, final int operacion, final int posicion )
  {
    int cont;
    String cad = "";

    if( operacion == 0 )
    {
      // Unir
      // Join
      cont = _iIdentLabel + 1;

      if( izq )
      {
        cad = _alstOriginal.get( _iIdentLabel ).toString();
        cad = cad.concat( " " );
        cad = cad.concat(_alstOriginal.get( _iIdentLabel + 1 ) );
        _alstOriginal.set( _iIdentLabel, cad.trim() );

        for( ; cont < _topArrays; cont++ )
          _alstOriginal.set(cont, _alstOriginal.get( cont + 1 ) );

        _alstOriginal.set( _alstOriginal.size() - 1, "" );
      }
      else
      {
        cad = _alstTranslation.get( _iIdentLabel );
        cad = cad.concat( " " );
        cad = cad.concat(_alstTranslation.get( _iIdentLabel + 1 ) );
        _alstTranslation.set( _iIdentLabel, cad.trim() );

        for( ; cont < _topArrays; cont++ )
          _alstTranslation.set(cont, _alstTranslation.get( cont + 1 ) );

        _alstTranslation.set( _alstTranslation.size() - 1, "" );
      }
    }
    else
      if( operacion == 1 )
      {
        // Borrar
        // Delete
        cont = _iIdentLabel;

        if( izq )
        {
          for( ; cont < _topArrays; cont++ )
            _alstOriginal.set(cont, _alstOriginal.get( cont + 1 ) );

          _alstOriginal.set( _alstOriginal.size() - 1, "" );
        }
        else
        {
          for( ; cont < _topArrays; cont++ )
            _alstTranslation.set(cont, _alstTranslation.get( cont + 1 ) );

          _alstTranslation.set( _alstTranslation.size() - 1, "" );
        }
      }
      else
      {
        // Split
        // Separar
        if( izq )
        {
          cont = _alstOriginal.size() - 1;

          if( _alstOriginal.get( _alstOriginal.size() - 1 ) != null &&
            !_alstOriginal.get( _alstOriginal.size() - 1 ).equals( "" ) )
            _alstOriginal.add(_alstOriginal.size(), _alstOriginal.get( _alstOriginal.size() - 1 ) );
          else
            _alstOriginal.set( _alstOriginal.size() - 1, _alstOriginal.
              get( _alstOriginal.size() - 2 ).toString() );

          for( ; cont > ( _iIdentLabel + 1 ); cont-- )
            _alstOriginal.set(cont, _alstOriginal.get( cont - 1 ) );

          if( _iIdentLabel < _topArrays )
          {
            cad = _alstOriginal.get( _iIdentLabel );

            if( posicion == 0 ) _alstOriginal.set( cont - 1, "" );
            else _alstOriginal.set( cont - 1, cad.substring( 0, posicion ).trim() );

            _alstOriginal.set( cont, cad.substring( posicion ).trim() );
          }
          else
          {
            cad = _alstOriginal.get( _iIdentLabel );

            if( posicion == 0 ) _alstOriginal.set( _alstOriginal.size() - 2, "" );
            else _alstOriginal.set( _alstOriginal.size() - 2, cad.substring( 0, posicion ).trim() );

            _alstOriginal.set( _alstOriginal.size() - 1, cad.substring( posicion ).trim() );
          }
        }
        else
        {
          // Meta
          cont = _alstTranslation.size() - 1;

          if( ( _alstTranslation.get( _alstTranslation.size() - 1 ) != null ) &&
            !( _alstTranslation.get( _alstTranslation.size() - 1 ).equals( "" ) ) )
            _alstTranslation.add( _alstTranslation.size(), _alstTranslation.
              get( _alstTranslation.size() - 1 ).toString() );
          else
            _alstTranslation.set( _alstTranslation.size() - 1, _alstTranslation.
              get( _alstTranslation.size() - 2 ).toString() );

          for( ; cont > ( _iIdentLabel + 1 ); cont-- )
            _alstTranslation.set( cont, _alstTranslation.get( cont - 1 ).toString() );

          if( _iIdentLabel < _topArrays )
          {
            cad = _alstTranslation.get( _iIdentLabel ).toString();

            if( posicion == 0 ) _alstTranslation.set( cont - 1, "" );
            else _alstTranslation.set( cont - 1, cad.substring( 0, posicion ).trim() );

            _alstTranslation.set( cont, cad.substring( posicion ).trim() );
          }
          else
          {
            cad = _alstTranslation.get( _iIdentLabel ).toString();

            if( posicion == 0 ) _alstTranslation.set( _alstTranslation.size() - 2, "" );
            else _alstTranslation.set( _alstTranslation.size() - 2, cad.substring( 0, posicion ).trim() );

            _alstTranslation.set( _alstTranslation.size() - 1, cad.substring( posicion ).trim() );
          }
        }
      }

    updateAlignmentsView();
  }


  /**
   *  Funci�n MostrarTabla. Esta funci�n actualiza las filas de la tabla con las
   *  modificaciones realizadas, a�ade filas o las quita..
   *
   *  Function MostrarTabla. This function updates the rows in the table with the 
   *  modifications performed, adds rows or removes them.
   */
  private void updateAlignmentsView()
  {
    if( !_alstOriginal.isEmpty() && !_alstTranslation.isEmpty() ) matchArrays();

    for( int cont = 0; cont < _vwAlignments.getRowCount(); cont++ )
    {
      _vwAlignments.setModelValueAt( "", cont, 0 );
      _vwAlignments.setModelValueAt( "", cont, 1 );
      _vwAlignments.setModelValueAt( "", cont, 2 );
    }

    if( ( _vwAlignments.getRowCount() > _alstOriginal.size() ) &&
      ( _alstOriginal.size() > 25 ) )
    {
      while( _vwAlignments.getRowCount() != _alstOriginal.size() )
      {
        _vwAlignments.removeSegment( _vwAlignments.getRowCount() - 1 );
        _vwAlignments.setPreferredSize( 805, 15, -1 );
      }
    }
    else
      if( _vwAlignments.getRowCount() < _alstOriginal.size() )
        while( _vwAlignments.getRowCount() != _alstOriginal.size() )
        {
          Segment nSeg = new Segment( null, null, null );
          _vwAlignments.addModelSegment( nSeg );
          _vwAlignments.setPreferredSize( 805, 15, 1 );
        }

    for( int cont = 0; cont < _alstOriginal.size(); cont++ )
    {
      _vwAlignments.setModelValueAt( Integer.toString( cont + 1 ), cont, 0 );
      _vwAlignments.setModelValueAt( _alstOriginal.get( cont ), cont, 1 );
    }

    for( int cont = 0; cont < _alstTranslation.size(); cont++ )
      _vwAlignments.setModelValueAt( _alstTranslation.get( cont ), cont, 2 );

    if( _iIdentLabel == _topArrays )
      _vwAlignments.setRowSelectionInterval( _topArrays, _topArrays );

    _vwAlignments.repaint();

    _edLeftSegment.setText( formatText( _vwAlignments.getValueAt(
        _iIdentLabel, 1 ).toString() ) );
    _edRightSegment.setText( formatText( _vwAlignments.getValueAt(
        _iIdentLabel, 2 ).toString() ) );
  }


 
  /**
   *  Funci�n IgualarArrays. Esta funci�n a�ade filas al array del menor tama�o y
   *  borra si las filas est�n en blanco.
   *
   *  Function IgualarArrays: adds rows to the smallest array and 
   *  deletes blank rows.
   */
  private void matchArrays()
  {
    boolean limpiar = true;

    while( _alstOriginal.size() > _alstTranslation.size() )
      _alstTranslation.add( _alstTranslation.size(), "" );

    while( _alstTranslation.size() > _alstOriginal.size() )
      _alstOriginal.add( _alstOriginal.size(), "" );

    while( limpiar )
    { // Borrar las l�neas en blanco del final
      // Delete blank lines at the end
      if( _alstOriginal.get( _alstOriginal.size() - 1 ) == null
          || ( _alstOriginal.get( _alstOriginal.size() - 1 ).equals( "" ) )
          && ( _alstTranslation.get( _alstTranslation.size() - 1 ) == null || _alstTranslation.get(
              _alstTranslation.size() - 1 ).equals( "" ) ) )
      {
        _alstOriginal.remove( _alstOriginal.size() - 1 );
        _alstTranslation.remove( _alstTranslation.size() - 1 );
      }
      else limpiar = false;
    }

    _topArrays = _alstOriginal.size() - 1;

    if( _iIdentLabel > ( _alstOriginal.size() - 1 ) )
      _iIdentLabel = _alstOriginal.size() - 1;
  }


  //  Accessed by ControlView
  final void onOriginalJoin()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    join( true );
  }

  //  Accessed by ControlView
  final void onOriginalDelete()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    delete( true );
  }

  //  Accessed by ControlView
  final void onOriginalSplit()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    split( true );
  }

  //  Accessed by ControlView
  final void onTranslationJoin()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    join( false );
  }

  //  Accessed by ControlView
  final void onTranslationDelete()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    delete( false );
  }

  //  Accessed by ControlView
  final void onTranslationSplit()
  {
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;
    split( false );
  }

  //  Accessed by ControlView
  final void onUndo()
  {
    undoChanges();
    _alstChanges.remove( _iChanges );
    _iChanges--;

    if( _iChanges == -1 ) _vwControls.setUndoEnabled( false );
  }


  /**
   *  Funci�n DeshacerBorrar. Esta funci�n se encarga de recuperar el �ltimo
   *  borrado realizado.
   *  Function DeshacerBorrar: undoes the last delete
   */
  private void undoDelete()
  {
    SegmentChanges ultChanges = _alstChanges.get( _iChanges );

    _iIdentLabel = ultChanges.getIdent_linea();
    boolean izq = ultChanges.getSource();

    if( izq )
    {
      // Parte Izquierda
      // Left part
      if( _iIdentLabel == _alstOriginal.size() )
      {
        // Reponer el borrado de �ltima l�nea
        // Revert the deleting of the last line
        _alstOriginal.add(_iIdentLabel, ultChanges.getFrase() );

        if( _alstOriginal.size() != _alstTranslation.size() )
          _alstTranslation.add( _alstTranslation.size(), "" );
      }
      else
      { 
        // Borrados intermedios
        // Intermediate deletions
        int cont = _alstOriginal.size() - 1;
        _alstOriginal.add( _alstOriginal.size(), _alstOriginal.
          get( _alstOriginal.size() - 1 ).toString() );

        for( cont = _alstOriginal.size() - 1; cont > _iIdentLabel; cont-- )
          _alstOriginal.set( cont, _alstOriginal.get( cont - 1 ).toString() );

        _alstOriginal.set( _iIdentLabel, ultChanges.getFrase().toString() );
      }
    }
    else
    {
      // Parte derecha
      // Right part
      if( _iIdentLabel == _alstTranslation.size() )
      {
        _alstTranslation.add( _iIdentLabel, ultChanges.getFrase().toString() );

        if( _alstOriginal.size() != _alstTranslation.size() )
          _alstOriginal.add( _alstOriginal.size(), "" );
      }
      else
      {
        int cont = _alstTranslation.size() - 1;
        // El texto origen ten�a como alineamiento una cadena vac�a, se repone
        // The source text had an empty string aligned: restore
        _alstTranslation.add( _alstTranslation.size(), _alstTranslation.
          get( _alstTranslation.size() - 1 ).toString() );

        for( cont = _alstTranslation.size() - 1; cont > _iIdentLabel; cont-- )
          _alstTranslation.set(cont, _alstTranslation.get( cont - 1 ) );

        _alstTranslation.set(_iIdentLabel, ultChanges.getFrase() );
      }
    }

    updateAlignmentsView();
  }


  /**
   *  Undo last change
   *
   *  Funci�n DeshacerCambios. Esta funci�n recupera el �ltimo cambio realizado.
   */
  private void undoChanges()
  {
    String cad = "";
    //StructCambios ultCambio = new StructCambios();
    SegmentChanges ultChanges = new SegmentChanges();
    int tam = 0;

    ultChanges = _alstChanges.get( _iChanges );
    _iIdentLabel = ultChanges.getIdent_linea();
    int operacion  = ultChanges.getTipo();
    int posicion   = ultChanges.getPosition();
    boolean izq    = ultChanges.getSource();
    _identAnt = _iIdentLabel;

    switch( operacion )
    {
      //if( operacion == 0 )
      case 0:
      {
        // El complementario de Unir es Split
        // The complement of Join is Split
        final String cadaux = ultChanges.getFrase();

        if( izq )
        {
          cad = _alstOriginal.get( _iIdentLabel );

          if( !cad.equals( "" ) ) cad = cad.trim();

          posicion = cad.indexOf( cadaux ) + cadaux.length();
        }
        else
        {
          cad = _alstTranslation.get( _iIdentLabel );

          if( !cad.equals( "" ) ) cad = cad.trim();

          posicion = cad.indexOf( cadaux ) + cadaux.length();
        }

        modifyAlignments( ultChanges.getSource(), 2, posicion );

        break;
      }

      //else if( operacion == 1 )
      case 1:
        // El complementario de Borrar es Insertar
        // The complement of Delete is Insert
        undoDelete();
        break;

      //else if( operacion == 2 )
      case 2:
      {
        // El complementario de Split es Unir
        // The complement of Split is Join
        int cont;

        // modifyAlignments(ultCambio.getFuente(), 0,
        // 0,ultCambio.getEliminada_fila());
        cont = _iIdentLabel + 1;

        if( izq )
        {
          cad = ultChanges.getFrase();
          _alstOriginal.set( _iIdentLabel, cad.trim() );

          while( cont < _topArrays )
          {
            _alstOriginal.set(cont, _alstOriginal.get( cont + 1 ) );
           cont++;
          }

          _alstOriginal.set( _alstOriginal.size() - 1, "" );
        }
        else
        {
          cad = ultChanges.getFrase();
          _alstTranslation.set( _iIdentLabel, cad.trim() );

          while( cont < _topArrays )
          {
            _alstTranslation.set(cont, _alstTranslation.get( cont + 1 ) );
            cont++;
          }

          _alstTranslation.set( _alstTranslation.size() - 1, "" );
        }

        updateAlignmentsView();

        break;
      }

      //else if( operacion == 3 )
      case 3:
      {
        // Se eliminaron lineas enteras en blanco que hay que recuperar
        // Blank lines were deleted and have to be restored
        tam = ultChanges.getTam();
        int[] filasEliminadas;

        filasEliminadas = ultChanges.getNumEliminada();

        while( tam > 0 )
        {
          // Se crean tantas filas como las que se eliminaron
          // Create as many files as those that were eliminated
          _alstTranslation.add( _alstTranslation.size(), "" );
          _alstOriginal.add( _alstOriginal.size(), "" );
          _topArrays = _alstTranslation.size() - 1;
          tam--;
        }

        int cont2 = _alstOriginal.size() - 1;
        tam = ultChanges.getTam();

        while( cont2 >= tam && tam > 0 )
        {
          // recorre los arrays para colocar las lineas en blanco donde
          // corresponden
          // moves across the arrays to place blank lines where needed
          if( cont2 == filasEliminadas[tam - 1] )
          {
            _alstTranslation.set( cont2, "" );
            _alstOriginal.set( cont2, "" );
            tam--;
          }
          else
          {
            _alstTranslation.set( cont2, _alstTranslation.get( cont2 - tam ).toString() );
            _alstOriginal.set(cont2, _alstOriginal.get( cont2 - tam ) );
          }

          cont2--;
        }

        updateAlignmentsView();

        break;
      }

      //else if( operacion == 4 )
      case 4:
      {
        // Separar UT
        // Split TU
        if( izq )
        {
          _alstTranslation.set( _iIdentLabel, _alstTranslation.get( _iIdentLabel + 1 ) );
          _alstOriginal.remove( _iIdentLabel + 1 );
          _alstTranslation.remove( _iIdentLabel + 1 );
        }
        else
        {
          _alstOriginal.set( _iIdentLabel, _alstOriginal.get( _iIdentLabel + 1 ) );
          _alstOriginal.remove( _iIdentLabel + 1 );
          _alstTranslation.remove( _iIdentLabel + 1 );
        }

        updateAlignmentsView();
      }
    }//  switch()

  }


  //  Accessed by AlignmentsView
  final void onTableClicked()
  {
    // if (e.getClickCount() == 1 || e.getClickCount() == 2) {
    _posTextArea = 0;

    if( _identAnt < _alstOriginal.size() )
    {
      //  ToDo: replace with docking editors call
      _alstOriginal.set( _identAnt, restoreText( _edLeftSegment.getText() ) );
      _alstTranslation.set( _identAnt, restoreText( _edRightSegment.getText() ) );
    }

    _edLeftSegment.setText(formatText( _vwAlignments.
      getValueAt( _vwAlignments.getSelectedRow(), 1 ).toString() ) );
    _edRightSegment.setText(formatText( _vwAlignments.
      getValueAt( _vwAlignments.getSelectedRow(), 2 ).toString() ) );

    _iIdentLabel = _vwAlignments.getSelectedRow();
    _identAnt = _iIdentLabel;

    if( _iIdentLabel == _topArrays )
    {
      _vwControls.setTranslationJoinEnabled( false );
      _vwControls.setOriginalJoinEnabled( false );
    }
    else
    {
      _vwControls.setTranslationJoinEnabled( true );
      _vwControls.setOriginalJoinEnabled( true );
    }
    // }

    updateAlignmentsView();
  }

  //  Accessed by AlignmentsView
  final void onTablePressed( final KeyEvent e )
  {
    int fila;

    if( _vwAlignments.getSelectedRow() != -1 )
    {
      fila = _vwAlignments.getSelectedRow();
      _posTextArea = 0;
    }
    else fila = 1;

    if( fila < _vwAlignments.getRowCount() - 1 )
    {
      if( ( e.getKeyCode() == KeyEvent.VK_DOWN )
          || ( e.getKeyCode() == KeyEvent.VK_NUMPAD2 ) )
      {
        if( _identAnt < _alstOriginal.size() )
        {
          _alstOriginal.set( _identAnt, restoreText( _edLeftSegment.getText() ) );
          _alstTranslation.set( _identAnt, restoreText( _edRightSegment.getText() ) );
        }

        _edLeftSegment.setText( formatText(  _vwAlignments.getValueAt( fila + 1, 1 ).toString() ) );
        _edRightSegment.setText( formatText( _vwAlignments.getValueAt( fila + 1, 2 ).toString() ) );

        _iIdentLabel = fila + 1;
      }
      else
        if( ( e.getKeyCode() == KeyEvent.VK_UP )
            || ( e.getKeyCode() == KeyEvent.VK_NUMPAD8 ) )
        {
          _iIdentLabel = fila - 1;

          if( fila == 0 )
          {
            fila = 1;
            _iIdentLabel = 0;
          }

          if( _identAnt < _alstOriginal.size() )
          {
            _alstOriginal.set( _identAnt, restoreText( _edLeftSegment.getText() ) );
            _alstTranslation.set( _identAnt,restoreText( _edRightSegment.getText() ) );
          }

          _edLeftSegment.setText(formatText( _vwAlignments.getValueAt( fila - 1, 1 ).toString() ) );
          _edRightSegment.setText( formatText( _vwAlignments.getValueAt( fila - 1, 2 ).toString() ) );
        }

      if( _iIdentLabel == _topArrays )
      {
        _vwControls.setTranslationJoinEnabled( false );
        _vwControls.setOriginalJoinEnabled( false );
      }
      else
      {
        _vwControls.setTranslationJoinEnabled( true );
        _vwControls.setOriginalJoinEnabled( true );
      }

      _identAnt = _iIdentLabel;
    }

    updateAlignmentsView();
  }

  //  Accessed by SegmentEditor
  final public void setTextAreaPosition( int iPos ) { _posTextArea = iPos; }

  /**
   *  Modificar_Idioma. Cuando se recoge la selecci�n del idioma por parte del
   *  usuario, se escribe en el fichero de configuraci�n y se actualiza la
   *  interfaz.
   *
   *  Modificar_Idioma. When the user selects a language, it is written in 
   *  the configuration file and the interface is updated.
   */
  private void updateUI()
  {
/*    try
    {
      //  Replace with preferences stuff
      //  Same code again? -RM
      FileWriter fw = new FileWriter( _fUserDir.getAbsoluteFile()
          + "/config.txt" );
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter pw = new PrintWriter( bw );

      pw.println( kIDIOMA );
      pw.println( _fUserHome.getAbsolutePath() );
      pw.println( _mncbSettingsLinebreak.isSelected() );
      pw.close();
*/

      updateUIText();

      TableColumn col = _vwAlignments.getColumnModel().getColumn( 1 );

      if( _fPathOriginal == null )
        col.setHeaderValue( getString( "TBL.HDR.COL.SOURCE" ) );
      else
        col.setHeaderValue( getString( "TBL.HDR.COL.SOURCE" ) + _fPathOriginal.getName() );

      col = _vwAlignments.getColumnModel().getColumn( 2 );

      if( _fPathTranslation == null )
        col.setHeaderValue( getString( "TBL.HDR.COL.TARGET" ) );
      else
        col.setHeaderValue( getString( "TBL.HDR.COL.TARGET" ) + _fPathTranslation.getName() );

      _vwAlignments.setColumnHeaderView();

      updateAlignmentsView();

    /*
    }
    catch( java.io.IOException ex )
    {
      System.out.println( l10n( "MSG_ERROR_LANGUAGE" ) );
    }
    */
  }


  /**
   *  Update interface text, using the current locale .
   *
   *  Actualizacion. Actualiza la interfaz con el idioma seleccionado por el
   *  usuario.
   *
   */
  private void updateUIText()
  {
    setTitle( getString( "WND.APP.TITLE" ) );
    _vwControls.updateText();

    _mnuFile       .setText( getString( "MNU.FILE" ) );
    _mniFileQuit   .setText( getString( "MNI.FILE.EXIT" ) );

    _mnuHelp       .setText( getString( "MNU.HELP" ) );
    _mniHelpAbout  .setText( getString( "MNI.HELP.ABOUT" ) );
    _mniHelpManual  .setText( getString( "MNI.HELP.MANUAL" ) );

    _mniFileOpen   .setActionCommand( getString( "MNI.FILE.OPEN" ) );
    _mniFileOpen   .setText( getString( "MNI.FILE.OPEN" ) );

    _mniFileClose.setText( getString( "MNI.FILE.ABORT" ) );

    _mnuSettings      .setText( getString( "MNU.SETTINGS" ) );

    _mniFileSaveAs.setText( getString( "MNI.FILE.SAVEAS" ) );

    _mncbSettingsLinebreak.setText( getString( "MNI.SETTINGS.LINEBREAK" ) );
    _mncbSettingsLinebreak.setToolTipText( getString(  "MNI.SETTINGS.LINEBREAK.TOOLTIP" ) );
  }

  //  ToDo: does nothing?
  private void onLinebreakToggle()
  {
    //  ToDo - replace with improved implementation in M1+ -RM
/*    try
    {
      //  Same code again !!! -RM
      FileWriter fw = new FileWriter( _fUserDir.getAbsoluteFile()
          + "/config.txt" );
      BufferedWriter bw = new BufferedWriter( fw );
      PrintWriter pw = new PrintWriter( bw );

      pw.println( kIDIOMA );
      pw.println( _fUserHome.getAbsolutePath() );
      pw.println( _mncbSettingsLinebreak.isSelected() );
      pw.close();
    }
    catch( java.io.IOException ex )
    {
      //  println only! -RM
      System.out.println( l10n( "MSG_ERROR_LANGUAGE" ) );
    }
*/
  }

  //  Accessed by ControlView currently
  final void onRemoveBlankRows()
  {
    int maxTamArrays = 0;
    int cont = 0;
    int lineasLimpiar = 0;
    //final int[] numEliminadas = new int [_kMAXBL];  // default = 1000
    final int[] numEliminadas = new int [1000];  // default = 1000 - why?
    int cont2 = 0;

    maxTamArrays = Utilities.largerSize(_alstOriginal.size(), _alstTranslation.size());

    while( cont <= ( maxTamArrays - lineasLimpiar ) )
    {
      if( ( _alstOriginal.get( cont ) == null || _alstOriginal.get( cont ).equals( "" ) )
          && ( _alstTranslation.get( cont ) == null || _alstTranslation.get( cont ).equals( "" ) ) )
      {
        lineasLimpiar++;
        numEliminadas[cont2] = cont + cont2;
        cont2++;
        _alstOriginal.remove( cont );
        _alstTranslation.remove( cont );
      }
      else cont++;
    }

    JOptionPane.showMessageDialog( this, getString( "MSG.ERASED" ) + " " +
      lineasLimpiar + " " + getString( "MSG.BLANK.ROWS" ) );


    // "Se han borrado n
    // filas en blanco"
    if( lineasLimpiar > 0 )
    {
      _iChanges++;

      SegmentChanges Changes = new SegmentChanges( 3, 0, false, "", 0 );
      _alstChanges.add( _iChanges, Changes );
      Changes.setNumEliminada( numEliminadas, lineasLimpiar );
      _vwControls.setUndoEnabled( true );
      updateAlignmentsView();
    }
  }

  //  Accessed by ControlView currently
  final void onTUSplit()
  {
    int izq;
    int cont;
    SegmentChanges Changes;
    //  Done in _vwControls
    //_btnUndo.setEnabled( true );
    _iChanges++;

    izq = _vwAlignments.getSelectedColumn();

    _alstOriginal.add(_alstOriginal.size(), 
            _alstOriginal.get( _alstOriginal.size() - 1 ) );
    _alstTranslation.add(_alstTranslation.size(), 
            _alstTranslation.get( _alstTranslation.size() - 1 ) );

    if( izq == 1 )
    {
      // Columna izq.
      // Left column.
      Changes = new SegmentChanges( 4, 0, true, "", _iIdentLabel );

      for( cont = _alstTranslation.size() - 1; cont > _iIdentLabel; cont-- )
      {
        _alstTranslation.set( cont, _alstTranslation.get( cont - 1 ).toString() );

        if( cont > ( _iIdentLabel + 1 ) )
          _alstOriginal.set( cont, _alstOriginal.get( cont - 1 ).toString() );
        else
          _alstOriginal.set( cont, "" );
      }

      _alstTranslation.set( _iIdentLabel, "" );
    }
    else
    {
      Changes = new SegmentChanges( 4, 0, false, "", _iIdentLabel );

      for( cont = _alstOriginal.size() - 1; cont > _iIdentLabel; cont-- )
      {
        _alstOriginal.set( cont, _alstOriginal.get( cont - 1 ).toString() );

        if( cont > ( _iIdentLabel + 1 ) )
          _alstTranslation.set( cont, _alstTranslation.get( cont - 1 ).toString() );
        else _alstTranslation.set( cont, "" );
      }

      _alstOriginal.set( _iIdentLabel, "" );
    }

    _alstChanges.add( _iChanges, Changes );
    updateAlignmentsView();
  }

  private void displayManual()
  {
    final Manual dlg = new Manual();

    dlg.setVisible( true );
  }


  /////////////////////////////////////////////////////////////////////
  //  Fonts
  //
  //  Note: all this fonts stuff will be refactored into its own class
  //  later so it will be independent

  /**
   *  Fonts mutator
   *  Delegates actual setting of fonts to specific methods
   *
   *  @param  Font
   *
   *  Passing in null causes default values to be used
   *  - used at startup or for reset  
   *  Passing in a font causes all UI elements to be the same 
   *  - used with the 'All' window area when selected in the fonts dialog  
   */
  final public void setFonts( final Font font )
  {
    //  Delegate
    setUserInterfaceFont( font );
    setTableFont( font );
    setTableHeaderFont( font );
    setSourceEditorFont( font );
    setTargetEditorFont( font );

    //  ToDo: 
    //_vwAlignments.setFonts( font );
    //_edLeftSegment.setFonts( font );
    //_edRightSegment.setFonts( font );
    _vwControls.setFonts( font );
  }


  /**
   *  Fonts accessor
   *
   *  @return Font[]
   */
  final public Font[] getFonts()
  {
    final Font[] afnt =
    {
      _fntUserInterface,
      _fntTable,
      _fntTableHeader,
      _fntSourceEditor,
      _fntTargetEditor
    };

    return( afnt );
  }


  /**
   *  User interface font mutator
   *
   *  @param  font
   */
  final public void setUserInterfaceFont( final Font font )
  {
    _fntUserInterface = font;

    if( _fntUserInterface != null ) 
    {
      //  Write to user preferences goes here
      //  To be done -RM

    }
    //  Default font (e.g. At startup from prefs file or for reset)    
    else
    {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  And supply a fallback default
      final String strFontName  = "Serif";
      final String strFontStyle = "Plain";
      final int iFontSize       = 11;

      //  Da font
      _fntUserInterface = new Font( strFontName, getFontStyle( strFontStyle ), iFontSize );
    }

    //  Use delegate to set actual UI fonts
    setUserInterfaceFonts( _fntUserInterface );
  }

  /**
   *  User interface components font mutator
   *  Acts as delegate for setUserInterfaceFont()
   *
   *  @param  font
   */
  final public void setUserInterfaceFonts( final Font font )
  {
    //  File menu
    _mnuFile        .setFont( font );
    _mniFileOpen    .setFont( font );
    _mniFileSave    .setFont( font );
    _mniFileSaveAs  .setFont( font );
    _mniFileClose   .setFont( font );

    if( !isMacOSX() ) _mniFileQuit.setFont( font );

    //  Settings menu
    _mnuSettings            .setFont( font );
    _mniSettingsFonts       .setFont( font );
    _mncbSettingsLinebreak  .setFont( font );

    if( !isMacOSX() )
    {
      _mnuLaf        .setFont( font );
      _mniLafLiquid  .setFont( font );
      _mniLafMetal   .setFont( font );
      _mniLafNimbus  .setFont( font );
      _mniLafSystem  .setFont( font );

      if( !isWindows() )  _mniLafGtk.setFont( font );
    }

    //  Help menu
    _mnuHelp        .setFont( font );
    _mniHelpManual  .setFont( font );

    if( !isMacOSX() )  _mniHelpAbout   .setFont( font );
  }

  /**
   *  User interface font accessor
   *
   *  @return Font
   */
  final public Font getUserInterfaceFont() { return( _fntUserInterface ); }


  /**
   *  Table header font mutator
   *
   *  @param  Font
   */
  final public void setTableHeaderFont( final Font font )
  {
    _fntTableHeader = font;

    if( _fntTableHeader != null )
    {
      //  Write to user preferences goes here
      //  To be done -RM

    }
    //  Default font (e.g. At startup from prefs file or for reset)    
    else
    {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  And supply a fallback default
      final String strFontName  = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize       = 11;

      //  Da font
      _fntTableHeader = new Font( strFontName, getFontStyle( strFontStyle ), iFontSize );
    }

    //  Set it in the source table
    //_tbl.getTableHeader().setFont( _fntTableHeader );
    //_vwAlignments.getTableHeader().setFont( _fntTableHeader );
  }

  /**
   *  Table header font accessor
   *
   *  @return Font
   */
  final public Font getTableHeaderFont() { return( _fntTableHeader ); }


  /**
   *  Table font mutator
   *
   *  @param  void
   *  @return Font
   */
  final public void setTableFont( final Font font )
  { 
    _fntTable = font;

    if( _fntTable != null )
    {
      //  Write to user preferences goes here
      //  To be done -RM

    }
    //  Default font (e.g. At startup from prefs file or for reset)    
    else
    {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName  = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize       = 11;

      //  Da font
      _fntTable = 
        new Font( strFontName, getFontStyle( strFontStyle ), iFontSize );  
    }

    //  Set it in the source table
    _vwAlignments.setTableFont( _fntTable );
  }

  /**
   *  Table font accessor
   *
   *  @param  void
   *  @return Font
   */
  final public Font getTableFont() { return( _fntTable ); }


  /**
   *  Original editor font mutator
   *
   *  @param  Font
   *  @return void
   */
  final public void setSourceEditorFont( final Font font )
  {
    _fntSourceEditor = font;

    if( _fntSourceEditor != null )
    {
      //  Write to user preferences goes here
      //  To be done -RM

    }
    //  Default font (e.g. At startup from prefs file or for reset)    
    else
    {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName  = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize       = 11;

      //  Da font
      _fntSourceEditor = 
        new Font( strFontName, getFontStyle( strFontStyle ), iFontSize );  
    }

    //  Set it in the source table
    _edLeftSegment.setEditorFont( _fntSourceEditor );
  }


  /**
   *  Original editor font accessor
   *
   *  @return font
   */
  final public Font getSourceEditorFont() { return( _fntSourceEditor ); }


  /**
   *  Translation editor font mutator
   *
   *  @param  font
   */
  final public void setTargetEditorFont( final Font font )
  {
    _fntTargetEditor = font;

    if( _fntTargetEditor != null )
    {
      //  Write to user preferences goes here
      //  To be done -RM
    }
    //  Default font (e.g. At startup from prefs file or for reset)    
    else
    {
      //  Read from user preferences goes here
      //  To be done -RM
      //  ...

      //  Or supply a fallback default
      final String strFontName  = "Dialog";
      final String strFontStyle = "Plain";
      final int iFontSize       = 11;

      //  Da font
      _fntTargetEditor = 
        new Font( strFontName, getFontStyle( strFontStyle ), iFontSize );
    }

    //  Set it in the target editor pane
    _edRightSegment.setEditorFont( _fntTargetEditor );
  }

  /**
   *  Translation editor font accessor
   *
   *  @return Font
   */
  final public Font getTargetEditorFont() { return( _fntTargetEditor ); }

  /**
   *  Font family names accessor
   *
   *  @return String[]
   */
  final public String[] getFontFamilyNames()
  {
    GraphicsEnvironment graphics =
      GraphicsEnvironment.getLocalGraphicsEnvironment();

    return( graphics.getAvailableFontFamilyNames() );
  }

  /**
   *  Font style string accessor
   *
   *  @param  font
   *  @return String
   */
  final public String getFontStyleString( final Font font )
  {
    final String strFontStyle;// = "";

    if( font.isBold() && font.isItalic() )  strFontStyle = "Bold+Italic";
    else if( font.isItalic() )              strFontStyle = "Italic";
    else if( font.isBold() )                strFontStyle = "Bold";
    else if( font.isPlain() )               strFontStyle = "Plain";
    else                                    strFontStyle = "Plain";

    return( strFontStyle );
  }


  /**
   *  Font style accessor
   *
   *  @param  strFontStyle
   *  @return int
   */
  final public int getFontStyle( final String strFontStyle )
  {
    final int iFontStyle;

    if( strFontStyle.equals( "Bold+Italic" ) )  iFontStyle = Font.BOLD + Font.ITALIC;
    else if( strFontStyle.equals( "Italic" ) )  iFontStyle = Font.ITALIC;
    else if( strFontStyle.equals( "Bold" ) )    iFontStyle = Font.BOLD;
    else if( strFontStyle.equals( "Plain" ) )   iFontStyle = Font.PLAIN;
    else                                        iFontStyle = Font.PLAIN;

    return( iFontStyle );
  }

  /**
   *  Display the fonts dialog to allow selection of fonts for
   *  origianl/translation tables/editors and main window
   *
   *  @param  void
   *  @return void
   */
  private void displayFontSelector()
  {
    FontSelector dlgFonts = new FontSelector( this, getFonts() );
    dlgFonts.setVisible( true );
  }

  //  WindowListener Overrides
  @Override
  final public void windowActivated( final WindowEvent evt ) {}
  @Override
  final public void windowClosed( final WindowEvent evt )    {}

  @Override
  final public void windowClosing( final WindowEvent evt )
  { if( evt.getSource() == this ) quit(); }

  @Override
  final public void windowDeactivated( final WindowEvent evt ) {}
  @Override
  final public void windowDeiconified( final WindowEvent evt ) {}
  @Override
  final public void windowIconified( final WindowEvent evt )   {}
  @Override
  final public void windowOpened( final WindowEvent evt )      {}

  /**
   *  class action listener implementation
   *
   *  @param  action
   */
  @Override
  final public void actionPerformed( final ActionEvent action )
  {
    final Object actor = action.getSource();

    if( actor instanceof JMenuItem )
    {
      if( actor == _mniFileOpen )            onOpen();
      else if( actor == _mniFileSave )       saveBitext();
      else if( actor == _mniFileSaveAs )     saveBitext();
      else if( actor == _mniFileClose )      onClose();
      else if( actor == _mniFileQuit )       quit();
      else if( actor == _mniSettingsFonts )  displayFontSelector();

      //  Only Linux, Solaris (UNIX?) with Gtk 2.2+
      else if( actor == _mniLafGtk )
      {
        try
        {
          UIManager.setLookAndFeel( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI( this );
        }
        catch( final Exception e )
        { System.out.println( getString( "OTP.LNF.INIT.ERROR" ) ); }
      }

      else if( actor == _mniLafLiquid )
      {
        try
        {
          UIManager.setLookAndFeel( "com.birosoft.liquid.LiquidLookAndFeel" );
          com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations( false );
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI( this );
        }
        catch( final Exception e )
        { System.out.println( getString( "OTP.LNF.INIT.ERROR" ) ); }
      }

      //  All platforms
      else if( actor == _mniLafMetal )
      {
        try
        {
          UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel" );
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI( this );
        }
        catch( final Exception e )
        { System.out.println( getString( "OTP.LNF.INIT.ERROR" ) ); }
      }

      //  Java 1.6 update 10+
      else if( actor == _mniLafNimbus )
      {
        try
        {
          UIManager.setLookAndFeel( "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel" );
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI( this );
        }
        catch( final Exception e )
        { System.out.println( getString( "OTP.LNF.INIT.ERROR" ) ); }
      }

      else if( actor == _mniLafSystem )
      {
        try
        {
          UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
          DockingUISettings.getInstance().updateUI();
          SwingUtilities.updateComponentTreeUI( this );
        }
        catch( final Exception e )
        { System.out.println( getString( "OTP.LNF.INIT.ERROR" ) ); }
      }

      else if( actor == _mniHelpManual )  displayManual();
      else if( actor == _mniHelpAbout )   displayAbout();
    }
  }
  
  /**
   *
   *  Extracts from the TMX those lines having information which is
   *  useful for alignment, and puts them in the corresponding ArrayList's
   *  The left part in _alstOriginal corresponds to source text lines and 
   *  the right part in _alstTranslation corresponds to the target text lines.
   *  Initialize the table with one line for each left and right line
   *
   *  Esta funci�n extrae del Tmx las l�neas que contienen
   *  informaci�n util para el alineamiento, y las mete en los ArrayList
   *  correspondientes. Parte izq. en _alstOriginal corresponde a las l�neas
   *  del texto fuente, y parte dcha. en _alstTranslation corresponde a las
   *  l�neas del texto meta.
   *  Inicializa la tabla con una fila por cada l�nea izq y dcha.
   */
  private void initializeAlignmentsView()
  {
    TableColumn col;

    col = _vwAlignments.getColumnModel().getColumn( 1 );
    col.setHeaderValue( getString( "TBL.HDR.COL.SOURCE" ) + _fPathOriginal.getName() );

    col = _vwAlignments.getColumnModel().getColumn( 2 );
    col.setHeaderValue( getString( "TBL.HDR.COL.TARGET" ) + _fPathTranslation.getName() );

    _vwAlignments.setColumnHeaderView();

    updateAlignmentsView();
    _topArrays = _alstOriginal.size() - 1;
    _iIdentLabel = 0;
  }

}// Bitext2tmxWindow{}


