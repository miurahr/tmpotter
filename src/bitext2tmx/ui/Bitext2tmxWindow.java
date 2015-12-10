/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2005-2006 Susana Santos Antón
#            (C) 2006-2009 Raymond: Martin et al
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

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

import java.io.*;
import java.util.*;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.*;

import com.vlsolutions.swing.docking.*;
import com.vlsolutions.swing.docking.event.*;
import com.vlsolutions.swing.docking.ui.DockingUISettings;

import bitext2tmx.engine.BitextModel;
import bitext2tmx.engine.Segment;
import bitext2tmx.engine.SegmentChanges;

import bitext2tmx.util.AquaAdapter;
import bitext2tmx.util.Localization;
import bitext2tmx.util.Utilities;

import static org.openide.awt.Mnemonics.*;

import static bitext2tmx.util.AquaAdapter.*;
import static bitext2tmx.util.Constants.*;
import static bitext2tmx.util.Localization.*;
import static bitext2tmx.util.Utilities.*;


/**
*
*/
final public class Bitext2tmxWindow extends JFrame implements ActionListener,
  WindowListener
{
  final private static long serialVersionUID = -540065960678391862L;

  private DockingDesktop  _Desktop;

  final private AlignmentsView  _vwAlignments   = new AlignmentsView( this );
  final private SegmentEditor   _edLeftSegment  = new SegmentEditor( this );
  final private SegmentEditor   _edRightSegment = new SegmentEditor( this );
  final private ControlsView    _vwControls     = new ControlsView( this );

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
  private JMenuItem  _mniSettingsRegexp;
  private JMenuItem  _mncbSettingsLinebreak;

  //  Language menu
  private JMenu      _mnuLocale;         //  Order by ISO-639 codes, not name
  private JMenuItem  _mniLocaleCatalan;  //  ca
  private JMenuItem  _mniLocaleEnglish;  //  en
  private JMenuItem  _mniLocaleSpanish;  //  es
  private JMenuItem  _mniLocaleFrench;   //  fr
  private JMenuItem  _mniLocaleJapanese; //  ja

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

  final private ArrayList  _alstBitext      = new ArrayList();
  final private ArrayList  _alstOriginal    = new ArrayList();
  final private ArrayList  _alstTranslation = new ArrayList();
  final private ArrayList<SegmentChanges>  _alstChanges =
    new ArrayList<SegmentChanges>();
  final private ArrayList  _alstLang        = new ArrayList();

  // ToDo: Check usage, Why 47?
  final private int  _KTAMTEXTAREA = 47;

  private int  _topArrays;    //  =  0;
  private int  _posTextArea;  //  =  0;
  private int  _iChanges     = -1;
  private int  _iChangesLB;   //   =  0;
  private int  _iIdentLabel;  //  =  0;
  private int  _identAnt;     //  =  0;

  private String  _strExpReg          = "";
  private String  _strLangOriginal    = "en";
  private String  _strLangTranslation = "en";
  private String  _strOriginal;
  private String  _strTranslation;
  //  ToDo:  check usage, cleanup, etc.
  //private String  _pathDestino;
  //private String  _strNewBitext;
  //private String  bitexto;
  //final private String _KREGEX = "\\.\\s|\\..\\s|\\;\\s|\\:\\s|\\?\\s|\\!\\s";

  private File  _fUserHome   = new File( System.getProperty( "user.home" ) );
  private File  _fPathOriginal;
  private File  _fPathTranslation;
  //final private File _fUserDir = new File( System.getProperty( "user.dir" ) );

  //  ToDo:  check usage, cleanup, etc.
  //private boolean  fichCreado  = false;
  //private int      _iExitButton   = 2;
  //private String   cod_fuente;
  //private String   cod_meta;
  //private String   _strListNumber = null;

  //private int     kIDIOMA = 2;
  private String  _ult_recorridoinv;

  private Font  _fntUserInterface;
  private Font  _fntTableHeader;
  private Font  _fntTable;
  private Font  _fntSourceEditor;
  private Font  _fntTargetEditor;

  private int   _shortcutKey;
  private char  _advancer;

  private String _strTMXEnc;

  public Bitext2tmxWindow()
  {
    initDockingUI();
    makeUI();
    //  ToDo: some things like these, etc.
    //updateWindowTitle();
    //loadDisplayPrefs() ?
    setWindowIcon();
    //initScreenLayout();

    //  Proxy callbacks from/to Mac OS X Aqua global menubar for Quit and About
    try
    {
      AquaAdapter.connect( this, "displayAbout", AquaEvent.ABOUT );
      AquaAdapter.connect( this, "quit", AquaEvent.QUIT );
    }
    catch( final NoClassDefFoundError e )
    {
     //  ToDo: log errors
     //log( e );
     //  Temporarily
     System.out.println( e );
    }

    setDefaultCloseOperation( javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE );
    addWindowListener( new WindowAdapter()
      { final public void windowClosing( final WindowEvent e ) { quit(); } } );

    final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    final Dimension frameSize = this.getSize();

    if( frameSize.height > screenSize.height ) frameSize.height = screenSize.height;
    if( frameSize.width > screenSize.width ) frameSize.width = screenSize.width;

    this.setLocation( ( screenSize.width - frameSize.width ) / 2,
      ( screenSize.height - frameSize.height ) / 2 );

    setFonts( null );

  }


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


    UIManager.put( "DockViewTitleBar.closeButtonText",    l10n( "VW.TITLEBAR.BTNCLOSE" ) );
    UIManager.put( "DockViewTitleBar.minimizeButtonText", l10n( "VW.TITLEBAR.BTNMINIMIZE" ) );
    UIManager.put( "DockViewTitleBar.maximizeButtonText", l10n( "VW.TITLEBAR.BTNMAXIMIZE" ) );
    UIManager.put( "DockViewTitleBar.restoreButtonText",  l10n( "VW.TITLEBAR.BTNRESTORE" ) );
    UIManager.put( "DockViewTitleBar.floatButtonText",    l10n( "VW.TITLEBAR.BTNFLOAT" ) );
    UIManager.put( "DockViewTitleBar.attachButtonText",   l10n( "VW.TITLEBAR.BTNATTACH" ) );

    UIManager.put( "DockTabbedPane.closeButtonText",    l10n( "TAB.BTNCLOSE" ) );
    UIManager.put( "DockTabbedPane.minimizeButtonText", l10n( "TAB.BTNMINIMIZE" ) );
    UIManager.put( "DockTabbedPane.restoreButtonText",  l10n( "TAB.BTNRESTORE" ) );
    UIManager.put( "DockTabbedPane.maximizeButtonText", l10n( "TAB.BTNMAXIMIZE" ) );
    UIManager.put( "DockTabbedPane.floatButtonText",    l10n( "TAB.BTNFLOAT" ) );

  }

  private ImageIcon getDesktopIcon( final String iconName )
  {
    if( isMacOSX() )
      return( Utilities.getIcon( "desktop/osx/" + iconName, this ) );

    return( Utilities.getIcon( "desktop/" + iconName, this ) );
  }

  private ImageIcon getIcon( final String iconName )
  { return( Utilities.getIcon( iconName, this ) ); }

  /**  Set root window icon */
  private void setWindowIcon()
  {
    try{ setIconImage( getIcon( "b2t-icon-small.png" ).getImage() ); }
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

    //  ToDo: add user configuration setting for desktop style, later...
    //DockingPreferences.setShadowDesktopStyle();
    //DockingPreferences.setFlatDesktopStyle();

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

    keyLeftSegment.setName( l10n( "VW.ORIGINAL.NAME" ) );
    keyLeftSegment.setTooltip( l10n( "VW.ORIGINAL.TOOLTIP" ) );
    keyRightSegment.setName( l10n( "VW.TRANSLATION.NAME" ) );
    keyRightSegment.setTooltip( l10n( "VW.TRANSLATION.TOOLTIP" ) );

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

    //  ToDo: check these settings
    //keyAlignmentTable  .setResizeWeight( 0.5f );
    //keyLeftSegment     .setResizeWeight( 0.25f );
    //keyRightSegment    .setResizeWeight( 0.25f );
    keySegmentButtons  .setResizeWeight( 0.1f );

    //  ToDo: check these settings
    //_Desktop.setDockableHeight( _vwAlignments, 0.25 );
    //_Desktop.setDockableHeight( _vwControls, 0.25 );
    //_Desktop.setDockableHeight( _edLeftSegment, 0.50 );

    _Desktop.addDockable( _vwAlignments );

    _Desktop.split( _vwAlignments, _edLeftSegment, DockingConstants.SPLIT_BOTTOM );
    //_Desktop.split( _edLeftSegment, _edRightSegment, DockingConstants.SPLIT_RIGHT );
    _Desktop.split( _edLeftSegment, _vwControls, DockingConstants.SPLIT_BOTTOM );
    _Desktop.split( _edLeftSegment, _edRightSegment, DockingConstants.SPLIT_RIGHT );

    //  ToDo: enable read/write of desktop configuration - code from OmegaT+ here
/*
    try
    {
      final File fWorkspace = new File( getConfigDirSlash() + CONF_FILE_WKSP );
      final BufferedInputStream in;

      if( fWorkspace.exists() )
        in = new BufferedInputStream( new FileInputStream( fWorkspace ) );
      else
        in = new BufferedInputStream( getClass().
          getResourceAsStream( "/net/sf/bitext2tmx/ui/gui/resources/OmegaT+.wksp" ) );

      _Desktop.readXML( in );

      in.close();
    }
    catch( final IOException ioe )
    {
      //  ToDo: Log message, localize -RM
      System.out.
        println( "Error reading workspace configuration: IOException" );
    }
    catch( final SAXException se )
    {
      //  ToDo: Log message, localize -RM
      System.out.
        println( "Error reading workspace configuration: SAXException" );
    }
    catch( final ParserConfigurationException pce )
    {
      //  ToDo: Log message, localize
      System.out.
        println( "Error reading workspace configuration: ParserConfigurationException" );
    }
*/

    //  ToDo: set up proper display preferences for loading GUI
    //  reuse code from OmegaT+ first
    //  Use standard 4:3 TV aspect ratio, if possible
    this.setSize( new Dimension( 800, 600 ) );
    //this.setSize( new Dimension( 640, 480 ) );
    this.setMinimumSize( new Dimension( 640, 480 ) );

    //setTitle( l10n( "WND_APP_TITLE" ) );
    //  ToDo: move/use updateWindowTitle() instead; change constant?
    setTitle( VERSIONNAME );

    //  Startup -> null. Will read from preferences saved
    //  values/use defaults
    //setFonts( null );

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

    //if( !isMacOSX() && accelerator != null ) mni.setAccelerator( accelerator );
    if( ksShortcut != null )  mni.setAccelerator( ksShortcut );
    if( !isMacOSX() && icon != null ) mni.setIcon( icon );

    mni.addActionListener( this );

    //  Localized text
    if( strKey != null ) setLocalizedText( mni, l10n( strKey ) );
    //if( strKey != null ) mni.setText( l10n( strKey ) );

    return( (T)mni );
  }

  private void addMenuSeparator( final JMenu menu ) { menu.addSeparator(); }

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

    _mniSettingsRegexp = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "Regular Expressions", "MNI.SETTINGS.REGEX" );

    //_mniSettingsRegexp.setEnabled( false );
    _mniSettingsRegexp.setEnabled( true );

    _mncbSettingsLinebreak = makeMenuComponent( MenuComponentType.CHECKBOX, null, null,
      "Linebreaks", "MNI.SETTINGS.LINEBREAK" );
    _mncbSettingsLinebreak.setToolTipText( l10n( "MNI.SETTINGS.LINEBREAK.TOOLTIP" ) );
    _mncbSettingsLinebreak.addChangeListener( new javax.swing.event.ChangeListener()
      { final public void stateChanged( final ChangeEvent e )
        { onLinebreakToggle();  } } );

    _mniSettingsFonts = makeMenuComponent( MenuComponentType.ITEM, null,
      getIcon( "fonts.png" ), "Configure Fonts...", "MNI.SETTINGS.FONTS" );
    _mniSettingsFonts.setToolTipText( l10n( "MNI.SETTINGS.FONTS.TOOLTIP" ) );

    _mnuLocale = makeMenuComponent( MenuComponentType.MENU, null, null,
      "Language", "MNU.LANGUAGE" );

    _mniLocaleCatalan = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "Catalan", "MNI.LANGUAGE.CA" );
    _mniLocaleCatalan.setToolTipText( l10n( "MNI.LANGUAGE.CA.TOOLTIP" ) );

    _mniLocaleEnglish = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "English", "MNI.LANGUAGE.EN" );
    _mniLocaleEnglish.setToolTipText( l10n( "MNI.LANGUAGE.EN.TOOLTIP" ) );

    _mniLocaleSpanish = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "Spanish", "MNI.LANGUAGE.ES" );
    _mniLocaleSpanish.setToolTipText( l10n( "MNI.LANGUAGE.ES.TOOLTIP" ) );

    _mniLocaleFrench = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "French", "MNI.LANGUAGE.FR" );
    _mniLocaleFrench.setToolTipText( l10n( "MNI.LANGUAGE.FR.TOOLTIP" ) );

    _mniLocaleJapanese = makeMenuComponent( MenuComponentType.ITEM, null, null,
      "Japanese", "MNI.LANGUAGE.JA" );
    _mniLocaleJapanese.setToolTipText( l10n( "MNI.LANGUAGE.JA.TOOLTIP" ) );

    _mnuHelp = makeMenuComponent( MenuComponentType.MENU, null, null,
      "Help", "MNU.HELP" );

    _mniHelpAbout = makeMenuComponent( MenuComponentType.ITEM, null,
      getIcon( "b2t-icon-small.png" ), "About", "MNI.HELP.ABOUT" );

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

    _mnuLocale.add( _mniLocaleCatalan );
    _mnuLocale.add( _mniLocaleEnglish );
    _mnuLocale.add( _mniLocaleSpanish );
    _mnuLocale.add( _mniLocaleFrench );
    _mnuLocale.add( _mniLocaleJapanese );

    _mnuSettings.add( _mncbSettingsLinebreak );
    _mnuSettings.add( _mniSettingsRegexp );
    _mnuSettings.add( _mniSettingsFonts );
    _mnuSettings.add( _mnuLocale );

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
    //int cont = 0;
    String cod_fuente;
    String cod_meta;

    while( !_alstOriginal.isEmpty() )    _alstOriginal.remove( 0 );
    while( !_alstTranslation.isEmpty() ) _alstTranslation.remove( 0 );

    final OpenTexts dlg = new OpenTexts();
    //dlg.dlgPath = _fUserHome;
    dlg.setPath( _fUserHome );
    dlg.setModal( true );
    dlg.setLocale( this.getLocale() );
    dlg.setVisible( true );

    if( !dlg.isClosed() )
    {
      _fUserHome = dlg.getPath();
      cod_fuente = (String)dlg.getSourceLangEncComboBox().getSelectedItem();
      //System.out.println( "Source endcoing" + cod_fuente );
      _fPathOriginal   = dlg.getSourcePath();
      _strOriginal     = dlg.getSource();
      _strLangOriginal = dlg.getSourceLocale();

      // prep the alignment view
      //_vwAlignments.clear();
      _vwAlignments.buildDisplay();

      if( dlg.getTypes() == 0 )
      {
        //getSelectedItem()
        cod_meta  = (String)dlg.getTargetLangEncComboBox().getSelectedItem();
        _fPathTranslation   = dlg.getTargetPath();
        _strTranslation     = dlg.getTarget();
        _strLangTranslation = dlg.getTargetLocale();

        // ? toggle?
        //readDocument( true );
        //readDocument( false );
        readDocument( true, cod_fuente );
        readDocument( false, cod_meta );
        boolean res = align();

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
        readTMX( _fPathOriginal, cod_fuente );
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
  *  Read in TMX
  *
  *  Esta funci�n lee un tmx y lo muestra.
  *  @param _fPathOriginal : el nombre y la ruta del tmx,
  *  @param cod_fuente : la codificaci�n que ha elegido el usuario
  *  para que sea abierto
  */
  private void readTMX( final File fPathOriginal )
  {
    String linea = "";
    int cont   = 0;
    int indice = 0; 
    int aux    = 0;
    boolean pI      = false;
    boolean salir   = false;
    boolean idioma1 = false;

    try
    {
      FileInputStream   fis = new FileInputStream( fPathOriginal );
      InputStreamReader isr = new InputStreamReader( fis, "ISO-8859-1" );
      BufferedReader    br  = new BufferedReader( isr );

      while( !salir && ( linea = br.readLine() ) != null )
        if( linea.contains( "<?xml version=\"1.0\" encoding=" ) )
          salir = true;

      _strTMXEnc = linea.substring( linea.indexOf( "encoding=" ) + 10,
        linea.indexOf( "\"?>" ) );
      br.close();
      isr.close();
      fis.close();
      fis = new FileInputStream( fPathOriginal );

      if( _strTMXEnc.equals( "ISO-8859-1" ) )
        isr = new InputStreamReader( fis, "ISO-8859-1" );
      else isr = new InputStreamReader( fis, "UTF-8" );

      br = new BufferedReader( isr );

      while( ( linea = br.readLine() ) != null )
      {
        linea = linea.trim();
        _alstBitext.add( cont, linea );
        cont++;

        //Si no tengo los idiomas
        if( !idioma1 )
        {
          if( linea.indexOf( "<tu tuid" ) != -1)   aux = 0;
          else if( linea.indexOf( "</tu>" ) != -1 )
          {
            if( aux == 2 ) idioma1 = true;
            else           aux = 0;
          }
          else if( linea.indexOf( "<tuv" ) != -1 )
          {
            if( aux == 0 )
            {
              indice = linea.indexOf( "lang=" );
              _strLangOriginal = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
            else
            {
              indice = linea.indexOf( "lang=" );
              _strLangTranslation = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
          }
        }
      }

      idioma1 = false;
      cont = 0;
      aux  = 0;

      while( cont<_alstBitext.size() )
      {
        linea = (String)_alstBitext.get( cont );

        while( linea.indexOf( "  " ) > - 1 )
        {
          linea = linea.substring( 0, linea.indexOf( "  " ) ) +
            linea.substring( linea.indexOf( "  " ) + 1 );
        }

        if( linea.indexOf( "<tuv" ) != -1 )
        {
          if( linea.indexOf( _strLangOriginal ) != -1 )  pI = true;
          else if( linea.indexOf( _strLangTranslation ) != -1 )  pI = false;
        }
        else if( linea.indexOf( "<seg>" ) != -1 )
        {
          linea = removeTag( linea, "seg" );

          while( linea.indexOf( "  " ) > - 1 )
          {
            linea = linea.substring( 0, linea.indexOf( "  " ) ) +
              linea.substring( linea.indexOf( "  " ) + 1 );
          }

          if( pI )
          {
            _alstOriginal.add(aux,linea);
            _alstTranslation.add(aux,"");
            idioma1 = true;
            aux++;
          }
          else
          {
            if( !idioma1 )
            {
              _alstOriginal.add( aux,"" );
              _alstTranslation.add( aux, linea );
              aux++;
            }
            else _alstTranslation.set( _alstTranslation.size() - 1, linea );
          }
        }

        cont++;
      }

      br.close();
    }
    catch( final IOException ex ) { System.out.println( ex.getMessage() ); }

  }


  //  FixMe: only reads TMX 1.1-1.2
  //  ToDo: need to read TMX 1.1-1.4
 /**
  *  Read in TMX
  *  Esta función lee un tmx y lo muestra.
  *  @param fpathFuente : el nombre y la ruta del tmx,
  *  @param cod_fuente : la codificación que ha elegido el usuario
  *  para que sea abierto
  */
  private void readTMX( final File fPathOriginal, final String cod_fuente )
  {
    try
    {
      final FileInputStream fis = new FileInputStream( fPathOriginal );
      final InputStreamReader isr;

      if( cod_fuente.equals( l10n( "ENCODING.DEFAULT" ) ) )
        isr = new InputStreamReader( fis );
      else if(cod_fuente.equals( "UTF-8" ) )
        isr = new InputStreamReader( fis, "UTF-8" );
      else
        isr = new InputStreamReader(fis, "ISO-8859-1");

      final BufferedReader br = new BufferedReader( isr );
      String linea;
      int cont   = 0;
      int indice = 0;
      int aux    = 0;
      boolean pI      = false;
      boolean idioma1 = false;

      while( ( linea = br.readLine() ) != null ) 
      {
        linea = linea.trim();
        _alstBitext.add( cont, linea );
        cont++;

        //Si no tengo los idiomas
        if( !idioma1 )
        {
          if( linea.indexOf( "<tu tuid" ) != -1 ) aux = 0;
          else if( linea.indexOf( "</tu>" ) != -1 )
          {
            if( aux == 2 ) idioma1 = true;
            else aux = 0;
          }
          else if( linea.indexOf( "<tuv" ) != -1)
          {
            if( aux == 0 )
            {
              indice = linea.indexOf( "lang=" );
              _strLangOriginal = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
            else
            {
              indice = linea.indexOf( "lang=" );
              _strLangTranslation = linea.substring( indice + 6, indice + 8 );
              aux++;
            }
          }
        }
      }

      idioma1 = false;
      cont = 0;
      aux  = 0;

      while( cont < _alstBitext.size() )
      {
        linea = (String)_alstBitext.get( cont );

        while( linea.indexOf( "  " ) > -1 )
        {
          linea = linea.substring( 0, linea.indexOf( "  " ) ) +
            linea.substring( linea.indexOf( "  " ) + 1 );
        }

        if (linea.indexOf("<tuv") != -1)
        {
          if( linea.indexOf( _strLangOriginal ) != -1 )     pI = true;
          else if( linea.indexOf( _strLangTranslation ) != -1) pI = false;
        }
        else if( linea.indexOf( "<seg>" ) != -1 )
        {
          linea = removeTag( linea, "seg" );

          while( linea.indexOf( "  " ) > -1 )
          {
            linea = linea.substring( 0, linea.indexOf( "  " ) ) +
              linea.substring( linea.indexOf( "  " ) + 1 );
          }

          if( pI )
          {
            _alstOriginal.add( aux, linea );
            _alstTranslation.add( aux, "" );
            idioma1 = true;
            aux++;
          }
          else
          {
            if( !idioma1 )
            {
              _alstOriginal.add( aux, "" );
              _alstTranslation.add( aux, linea );
              aux++;
            }
            else _alstTranslation.set( _alstTranslation.size()-1, linea );
          }
        }

        cont++;
      }

      br.close();
    }
    catch( final java.io.IOException ex ) { System.out.println( ex ); }

  }


  /**
   *  Remove tag
   *  Esta funci�n elimina las etiquetas de abertura y cierre en la cadena.
   *  @param linea : la cadena que hay que retocar,
   *  @param etiqueta : que queremos quitar
   *  @return cad devuelve la cadena sin la etiqueta
   */
  private String removeTag( final String linea, final String etiqueta )
  {
    String cad = "";
    final String etiquetaCerrada = "</" + etiqueta + ">";

    //  FixMe: There is an issue with this being called repeatedly when
    //  a bad tag is located. this would be okay for a few tags, but a TMX
    //  with many tags will have this jump up many times, basically halting
    //  operation until the user presses OK for each one. Need an easy way
    //  out of this, rather than having to halt/crash the program. A cancel
    //  button and related functionality to jump out of the action in process!
    if( linea.indexOf( etiquetaCerrada ) == -1 )
      JOptionPane.showMessageDialog( null,
        "El TMX no está bien formado, falta una etiqueta de cierre de segmento" );
    else
      cad = linea.substring( etiqueta.length() + 2, linea.indexOf( etiquetaCerrada ) );

    return( cad );
  }


  /**
   *  Reads in document to string with the original or translation text
   *  so it can be segmented
   *
   *  Trocear Crea una cadena con el texto fuente o meta, seg�n
   *  par�metro, para despu�s partirla y tener segmentos.
   *
   *  @param kfuente :texto fuente o meta
   */
  private void readDocument( final boolean kfuente, final String strEncoding )
  {
    final StringBuilder documento = new StringBuilder();
    String doc = "";
    boolean limpiar = true;
    String linea = "";

    //System.out.println( "Trocear source: " + cod_fuente );
    //System.out.println( "Trocear target: " + cod_meta );
    //System.out.println( "strEncoding: " + strEncoding );

    try
    {
      final FileInputStream fis;

      // Dividing the source text in phrases
      // Vamos a partir en frases el texto fuente
      if( kfuente ) fis = new FileInputStream( _strOriginal );
      else fis = new FileInputStream( _strTranslation );

      final InputStreamReader isr;
      final BufferedReader br;

      if( strEncoding.equals(  l10n( "ENCODING.DEFAULT" ) ) )
        isr = new InputStreamReader( fis );
      else
        isr = new InputStreamReader( fis, strEncoding );

/*
      if( _strCodeOriginal.equals(  l10n( "ENCODING_DEFAULT" ) ) )
        isr = new InputStreamReader( fis );

      //  -> String constants
      else if( _strCodeOriginal.equals( "UTF-8" ) )
        isr = new InputStreamReader( fis, "UTF-8" );

      //  -> String constants
      else
        isr = new InputStreamReader( fis, "ISO-8859-1" );
*/

      br = new BufferedReader( isr );

      while( ( linea = br.readLine() ) != null )
      {
        linea = linea.trim();

        if( !linea.equals( "" ) )
        {
          linea = linea + "\n";
          documento.append( linea );
        }
        else
          if( !documento.equals( "" ) )
          {
            //  ToDo: test this out - what is the point here?
            //documento = documento.append( "\n" );
            documento.append( "\n" );
          }
      }

      doc = documento.toString();

      if( _mncbSettingsLinebreak.isSelected() )
        segmentWithBreak( doc, kfuente );
      else segmentWithoutBreak( doc, kfuente );

      while( _alstOriginal.size() > _alstTranslation.size() )
        _alstTranslation.add( _alstTranslation.size(), "" );

      while( _alstTranslation.size() > _alstOriginal.size() )
        _alstOriginal.add( _alstOriginal.size(), "" );

      while( limpiar )
      {
        if( ( ( _alstOriginal.get( _alstOriginal.size() - 1 ) == null ) || ( _alstOriginal
            .get( _alstOriginal.size() - 1 ).equals( "" ) ) )
            && ( ( _alstTranslation.get( _alstTranslation.size() - 1 ) == null ) || ( _alstTranslation
                .get( _alstTranslation.size() - 1 ).equals( "" ) ) ) )
        {
          _alstOriginal.remove( _alstOriginal.size() - 1 );
          _alstTranslation.remove( _alstTranslation.size() - 1 );
        }
        else limpiar = false;
      }
    }
    catch( final java.io.IOException ex )
    {
      JOptionPane.showMessageDialog( this, l10n( "MSG.ERROR" ),
        l10n( "MSG.ERROR.FILE.READ" ), JOptionPane.ERROR_MESSAGE );

      this.dispose();
    }
  }


  /**
   *
   *  Segments texts according to the programmed rules considering
   *  that a newline is not a segmentation boundary (two newlines
   *  are however)
   *
   *  @param documento
   *        :string containing the whole text
   *  @param kFuente
   *        :indicates whether it is the source or the target text
   *
   *  Esta funci�n trocea con las reglas programadas, considerando que un salto
   *  de l�nea no es una regla por la que partir. (Dos s� lo ser�an)
   *
   *  @param documento :cadena con el texto completo
   *  @param kFuente :indica si es el fichero fuente o el meta
   */
  private void segmentWithoutBreak( final String documento, final boolean kFuente )
  {
    String result = "";
    char car      = ' ';
    char carAnt   = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar   = false;

    for( int i = 0; i < documento.length(); i++ )
    {
      car = documento.charAt( i );

      //  This code is repeated in various places -> new method -RM
      if( car == '\n' || car == '\t' ) result = result + ' ';
      else result = result + car;

      if( car == ' ' )
      {
        if( carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' ||
          carAnt == '!' )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente ) _alstOriginal.add( cont, result.trim() );
            else _alstTranslation.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar  = false;
        }
        else if( carAnt == '"' && kpunto )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente )  _alstOriginal.add( cont, result );
            else _alstTranslation.add( cont, result );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( kpunto && kcar )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente ) _alstOriginal.add( cont, result );
            else _alstTranslation.add( cont, result );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
      }
      else if( car == '\n' && ( carAnt == '\n' || carAnt == '.' ) )
      {
        if( !result.equals( "" ) )
        {
          if( kFuente ) _alstOriginal.add( cont, result.trim() );
          else _alstTranslation.add( cont, result.trim() );
        }

        cont++;
        car = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar = false;
      }
      else if( car == '.' ) kpunto = true;
      /*
       * else if(car >= '0' && car <= '9' && kpunto){ kpunto = false; }
       */
      else kcar = true;

      carAnt = car;
    }

    if( !result.equals( "" ) )
    {
      if( kFuente ) _alstOriginal.add( cont, result );
      else          _alstTranslation.add( cont, result );
    }
  }

  private void segmentWithBreak( final String documento, final boolean kFuente )
  {
    String result = "";
    char car      = ' ';
    char carAnt   = ' ';
    int cont = 0;
    boolean kpunto = false;
    boolean kcar   = false;

    for( int i = 0; i < documento.length(); i++ )
    {
      car = documento.charAt( i );

      if( car == '\n' || car == '\t' ) result = result + ' ';
      else result = result + car;

      if( car == '\n' )
      {
        if( !result.equals( "" ) )
        {
          if( kFuente ) _alstOriginal.add( cont, result.trim() );
          else          _alstTranslation.add( cont, result.trim() );
        }

        cont++;
        car    = ' ';
        carAnt = ' ';
        result = "";
        kpunto = false;
        kcar   = false;
      }
      else if( car == ' ' )
      {
        if( carAnt == '.' || carAnt == ';' || carAnt == ':' || carAnt == '?' ||
          carAnt == '!' )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente ) _alstOriginal.add( cont, result.trim() );
            else          _alstTranslation.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( carAnt == '"' && kpunto )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente ) _alstOriginal.add( cont, result.trim() );
            else          _alstTranslation.add( cont, result.trim() );
          }

          cont++;
          car = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar = false;
        }
        else if( kpunto && kcar )
        {
          if( !result.equals( "" ) )
          {
            if( kFuente ) _alstOriginal.add( cont, result.trim() );
            else          _alstTranslation.add( cont, result.trim() );
          }

          cont++;
          car    = ' ';
          carAnt = ' ';
          result = "";
          kpunto = false;
          kcar   = false;
        }
      }
      else if( car == '.' ) kpunto = true;
      else kcar = true;

      carAnt = car;
    }

    if( !result.equals( "" ) )
    {
      if( kFuente ) _alstOriginal.add( cont, result.trim() );
      else          _alstTranslation.add( cont, result.trim() );
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
    col.setHeaderValue( l10n( "TBL.HDR.COL.SOURCE" ) + _fPathOriginal.getName() );

    col = _vwAlignments.getColumnModel().getColumn( 2 );
    col.setHeaderValue( l10n( "TBL.HDR.COL.TARGET" ) + _fPathTranslation.getName() );

    _vwAlignments.setColumnHeaderView();

    updateAlignmentsView();
    _topArrays = _alstOriginal.size() - 1;
    _iIdentLabel = 0;
  }


  /**
   *  Esta funcion recompone el Tmx a partir de las posibles modificaciones
   *  efectuadas y lo deja en el directorio correspondiente segun los cambios
   *  realizados.
   */
  private void writeBitext( final File fNombre )
  {
    int cont = 0;
    final FileOutputStream fw;
    final OutputStreamWriter osw;
    final BufferedWriter bw;
    final PrintWriter pw;
    int max = 0;

    try
    {
      fw = new FileOutputStream( fNombre );
      //osw = new OutputStreamWriter(fw,cod_TMX);_strTMXEnc
      osw = new OutputStreamWriter( fw, _strTMXEnc );
      bw = new BufferedWriter( osw );
      pw = new PrintWriter( bw );

      max = largersizeSegments();
      //pw.println("<?xml version=\"1.0\" encoding=\"" + cod_TMX + "\"?>"); //poner el encoding
      pw.println( "<?xml version=\"1.0\" encoding=\"" + _strTMXEnc + "\"?>" ); //poner
      pw.println( "<tmx version=\"1.4\">" );
      //pw.println( "  <header creationtool=\"Bitext2tmx\" " +
                  //"creationtoolversion=\"1.0\" segtype=\"sentence\" o-tmf=\"Bitext2tmx\" " +
                  //"adminlang=\"en\" srclang=\"" +
                  //_strLangOriginal.toLowerCase() + "\" " +
                  //"datatype=\"PlainText\"  o-encoding=\"" + _strTMXEnc + "\">" );

      //pw.println(  );
      //pw.println(  );
      pw.println( "  <header" );
      pw.println( "    creationtool=\"Bitext2tmx\"" );
      pw.println( "    creationtoolversion=\"1.0\"" );
      pw.println( "    segtype=\"sentence\"" );
      pw.println( "    o-tmf=\"Bitext2tmx\""  );
      pw.println( "    adminlang=\"en\"" );
      pw.println( "    srclang=\"" + _strLangOriginal.toLowerCase() + "\"" );
      pw.println( "    datatype=\"PlainText\"" );
      pw.println( "    o-encoding=\"" + _strTMXEnc + "\"" );
      pw.println( "  >" );

      pw.println( "  </header>" );
      pw.println( "  <body>" );

      while( cont <= max )
      {
        if( !( _alstOriginal.get( cont ).equals( "" ) ) &&
            !( _alstTranslation.get( cont ).equals( "" ) ) )
        {
          pw.println( "  <tu tuid=\"" + ( cont ) + "\" datatype=\"Text\">" );

          if( max >= cont )
          {
            if( !( _alstOriginal.get( cont ).equals( "" ) ) )
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
              //pw.println("      <seg>" + _alstOriginal.get( cont ) + "</seg>" );
              pw.println( "      <seg>" +
               getValidXMLText( (String)_alstOriginal.get( cont ) ) +
                "</seg>" );
              pw.println( "    </tuv>");
            }
            else
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
              pw.println( "      <seg>  </seg>");
              pw.println( "    </tuv>");
            }
          }

          if( max >= cont )
          {
            if( !( _alstTranslation.get( cont ).equals( "" ) ) )
            {
              pw.println( "    <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
              //pw.println("      <seg>" + _alstTranslation.get( cont ) + "</seg>");
              pw.println( "      <seg>" +
                getValidXMLText( (String)_alstTranslation.get( cont ) ) +
                 "</seg>" );
              pw.println( "    </tuv>");
            }
            else
            {
              pw.println("    <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
              pw.println("      <seg>  </seg>");
              pw.println("    </tuv>");
            }
          }

          pw.println( "  </tu>" );
        }
        //Para que no hayan unidades de traducci?n con elementos vac?os.
        cont++;
      }

      pw.println( "  </body>" );
      pw.println( "</tmx>" );
      pw.close();
    }
    catch( final IOException ex )
    {
      JOptionPane.
        showMessageDialog( this, (String)_alstLang.get( 21 ),
          (String)_alstLang.get( 18 ), JOptionPane.ERROR_MESSAGE );

      this.dispose();
    }
  }



  /**
   *  Esta funci�n recompone el Tmx a partir de las posibles modificaciones
   *  efectuadas y lo deja en el directorio correspondiente seg�n los cambios
   *  realizados
   *
   *  Rebuilds the TMX from the modifications possibly made to it and leaves
   *  it in the corresponding directory according to the changes performed
   *
   *  @param kNombre
   *    :indica si el usuario ha utilizado guardar o guardar como.
   *    :shows whether the user has used "save" or "save as"
   */
  private void writeBitext()
  {
    int cont = 0;
    int n    = 0;
    boolean guardar = false;
    boolean salir   = false;
    int returnVal   = 0;

    final FileOutputStream   fw;
    final OutputStreamWriter osw;
    final BufferedWriter     bw;
    final PrintWriter        pw;

    int max = 0;
    File fNombre;

    try
    {
      final String nombre = _fPathOriginal.getName().
        substring( 0, ( _fPathOriginal.getName().length() - 4 ) );

      fNombre = new File( nombre.concat( _strLangTranslation + ".tmx" ) );

      //  Resource bundles/Loc4J needed -RM
      while( !guardar && !salir )
      {
        final JFileChooser fc = new JFileChooser();

        //  switch() on language removed from here -RM

        boolean kNombre_Usuario = false;

        while( !kNombre_Usuario )
        {
          fc.setLocation( 230, 300 );
          fc.setCurrentDirectory( _fUserHome );
          fc.setDialogTitle( l10n( "DLG.SAVEAS" ) );

          fc.setMultiSelectionEnabled( false );
          fc.setSelectedFile( fNombre );
          fc.setFileSelectionMode( JFileChooser.FILES_ONLY );
          returnVal = fc.showSaveDialog( this );
          _fUserHome = fc.getCurrentDirectory();

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
            kNombre_Usuario = true;  //  para romper bucle
            salir = true;            //  to break the loop
          }
        }

        if( kNombre_Usuario && !salir )
        {
          //  Comprobar si ya existe el fichero
          //  Check whether the file exists
          if( fNombre.exists() )
          {
            // "Guardar"/"Save"  & "Cancelar"/"Cancel"
            final Object[] options = { l10n( "BTN.SAVE" ),
              l10n( "BTN.CANCEL" ) };

            n = JOptionPane.showOptionDialog( this, l10n( "MSG.FILE.EXISTS" ), l10n( "MSG.WARNING" ),
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

      if( guardar )
      {
        //  Pedir el encoding
        //  Ask for the encoding
        Encodings dlgEnc = new Encodings();

        dlgEnc.setVisible( true );

        if( !dlgEnc.isClosed() )
        {
        final String encoding = dlgEnc.getComboBoxEncoding();
        dlgEnc.dispose();

        fw  = new FileOutputStream( fNombre );
        osw = new OutputStreamWriter( fw, encoding );
        bw  = new BufferedWriter( osw );
        pw  = new PrintWriter( bw );

        max = largersizeSegments();

        //  Yuck! Move this hardcode string stuff out of the code -RM
        //  Recommendation: keep in a "template" type file?
        pw.println( "<?xml version=\"1.0\" encoding=\"" + encoding + "\"?>\n" );
         // poner
        // el
        // encoding
        pw.println( "<tmx version=\"1.4\">" );
        pw.println( "  <header\n" +
                    "    creationtool=\"Bitext2tmx\"\n" +
                    "    creationtoolversion=\"1.0.M0\"\n" +
                    "    segtype=\"sentence\"\n" +
                    "    o-tmf=\"Bitext2tmx\"\n" +
                    "    adminlang=\"en\"\n"     +
                    "    srclang=\"" + _strLangOriginal.toLowerCase() + "\"\n" +
                    "    datatype=\"PlainText\"\n" +
                    "    o-encoding=\"" + encoding + "\"\n" +
                    "  >" );
        pw.println( "  </header>" );
        pw.println( "  <body>" );

        while( cont <= max )
        {
          if( !( _alstOriginal.get( cont ).equals( "" ) ) &&
            !( _alstTranslation.get( cont ).equals( "" ) ) )
          {
            pw.println( "    <tu tuid=\"" + ( cont ) + "\" datatype=\"Text\">" );

            if( max >= cont )
            {
              if( !( _alstOriginal.get( cont ).equals( "" ) ) )
              {
                //pw.println( "  <tuv lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
                pw.println( "      <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() +  "\">" );
                //pw.println( "        <seg>" + _alstOriginal.get( cont ) + "</seg>" );
                //  Ensure that the text is valid for use in XML
                pw.println( "        <seg>" +
                  getValidXMLText( (String)_alstOriginal.get( cont ) ) +
                  "</seg>" );
                pw.println( "      </tuv>" );
              }
              else
              {
                //pw.println( "  <tuv lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
                pw.println( "      <tuv xml:lang=\"" + _strLangOriginal.toLowerCase() + "\">" );
                pw.println( "        <seg>  </seg>" );
                pw.println( "      </tuv>" );
              }
            }

            if( max >= cont )
            {
              if( !( _alstTranslation.get( cont ).equals( "" ) ) )
              {
                //pw.println( "  <tuv lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
                pw.println( "      <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
                //pw.println( "        <seg>" + _alstTranslation.get( cont ) + "</seg>" );
                //  Ensure that the text is valid for use in XML
                pw.println( "        <seg>" +
                  getValidXMLText( (String)_alstTranslation.get( cont ) ) );
                pw.println( "      </tuv>" );
              }
              else
              {
                //pw.println( "  <tuv lang=\"" + _strLangTranslation.toLowerCase() + "\">" );
                pw.println( "      <tuv xml:lang=\"" + _strLangTranslation.toLowerCase() + "\">");
                pw.println( "        <seg>  </seg>" );
                pw.println( "      </tuv>" );
              }
            }

            pw.println( "    </tu>" );
          }

          //  Para que no hayan unidades de traducci�n con elementos vac�os.
          //  To avoid having translation units having empty elements
          cont++;
        }

        pw.println( "  </body>" );
        pw.println( "</tmx>" );
        pw.close();
        //clear();
        //BotonesFalse();
        //_btnUndo.setEnabled( false );

      }
      }
    }
    catch( final java.io.IOException ex )
    {
      JOptionPane.showMessageDialog( this, l10n( "MSG.ERROR.FILE.WRITE" ),
        l10n( "MSG.ERROR" ), JOptionPane.ERROR_MESSAGE );

      this.dispose();
    }
  }


  /**
   *  Necessary capabilities to store the bitext
   *
   *  Esta funci�n tiene la funcionalidad para guardar el bitexto
   */
  private void saveBitext()
  {
    for( int cont = 0; cont < ( _alstOriginal.size() - 1 ); cont++ )
    {
      if( _alstOriginal.get( cont ).equals( "" ) && _alstTranslation.get( cont ).equals( "" ) )
      {
        _alstOriginal.remove( cont );
        _alstTranslation.remove( cont );
      }
    }

    //  New stuff does not work yet
    //  ToDo: determine use of new stuff and fix as necessary
    // new
    //  select encoding dialog -  currently not implemented
    //File fNombre = DlgNombreYCodif();
    //new
    //if( fNombre != null )

    writeBitext();
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
    _iChangesLB  = 0;
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

    if( textAreaIzq ) Changes.setFrase( _alstOriginal.get( _iIdentLabel ).toString() );
    else Changes.setFrase( _alstTranslation.get( _iIdentLabel ).toString() );

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
      if( _posTextArea >= _alstOriginal.get( _iIdentLabel ).toString().length() )
        _posTextArea = 0;
    }
    else
      if( _posTextArea >= _alstTranslation.get( _iIdentLabel ).toString().length() )
        _posTextArea = 0;

    final SegmentChanges Changes = new SegmentChanges( 2, _posTextArea, textAreaIzq, "",
        _iIdentLabel );
    _alstChanges.add( _iChanges, Changes );

    if( textAreaIzq ) Changes.setFrase( _alstOriginal.get( _iIdentLabel ).toString() );
    else Changes.setFrase( _alstTranslation.get( _iIdentLabel ).toString() );

    modifyAlignments( textAreaIzq, 2, Changes.getPos() );
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
        cad = cad.concat( _alstOriginal.get( _iIdentLabel + 1 ).toString() );
        _alstOriginal.set( _iIdentLabel, cad.trim() );

        for( ; cont < _topArrays; cont++ )
          _alstOriginal.set( cont, _alstOriginal.get( cont + 1 ).toString() );

        _alstOriginal.set( _alstOriginal.size() - 1, "" );
      }
      else
      {
        cad = _alstTranslation.get( _iIdentLabel ).toString();
        cad = cad.concat( " " );
        cad = cad.concat( _alstTranslation.get( _iIdentLabel + 1 ).toString() );
        _alstTranslation.set( _iIdentLabel, cad.trim() );

        for( ; cont < _topArrays; cont++ )
          _alstTranslation.set( cont, _alstTranslation.get( cont + 1 ).toString() );

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
            _alstOriginal.set( cont, _alstOriginal.get( cont + 1 ).toString() );

          _alstOriginal.set( _alstOriginal.size() - 1, "" );
        }
        else
        {
          for( ; cont < _topArrays; cont++ )
            _alstTranslation.set( cont, _alstTranslation.get( cont + 1 ).toString() );

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
            _alstOriginal.add( _alstOriginal.size(), _alstOriginal.get( _alstOriginal.size() - 1 ).toString() );
          else
            _alstOriginal.set( _alstOriginal.size() - 1, _alstOriginal.
              get( _alstOriginal.size() - 2 ).toString() );

          for( ; cont > ( _iIdentLabel + 1 ); cont-- )
            _alstOriginal.set( cont, _alstOriginal.get( cont - 1 ).toString() );

          if( _iIdentLabel < _topArrays )
          {
            cad = _alstOriginal.get( _iIdentLabel ).toString();

            if( posicion == 0 ) _alstOriginal.set( cont - 1, "" );
            else _alstOriginal.set( cont - 1, cad.substring( 0, posicion ).trim() );

            _alstOriginal.set( cont, cad.substring( posicion ).trim() );
          }
          else
          {
            cad = _alstOriginal.get( _iIdentLabel ).toString();

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
   *  Funci�n tamMax. Esta funci�n devuelve el tama�o del array mayor.
   *
   *  @return INT: tama�o del mayor array
   *
   *  Funci�n tamMax. This function returns the size of the largest array
   *
   *  @return INT: Size of the largest array
   */
  private int largersizeSegments()
  {
    int max = _alstOriginal.size() - 1;

    if( _alstTranslation.size() > _alstOriginal.size() )
      max = _alstTranslation.size() - 1;

    return( max );
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
    boolean izq = ultChanges.getFuente();

    if( izq )
    {
      // Parte Izquierda
      // Left part
      if( _iIdentLabel == _alstOriginal.size() )
      {
        // Reponer el borrado de �ltima l�nea
        // Revert the deleting of the last line
        _alstOriginal.add( _iIdentLabel, ultChanges.getFrase().toString() );

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
          _alstTranslation.set( cont, _alstTranslation.get( cont - 1 ).toString() );

        _alstTranslation.set( _iIdentLabel, ultChanges.getFrase().toString() );
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
    int posicion   = ultChanges.getPos();
    boolean izq    = ultChanges.getFuente();
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
          cad = _alstOriginal.get( _iIdentLabel ).toString();

          if( !cad.equals( "" ) ) cad = cad.trim();

          posicion = cad.indexOf( cadaux ) + cadaux.length();
        }
        else
        {
          cad = _alstTranslation.get( _iIdentLabel ).toString();

          if( !cad.equals( "" ) ) cad = cad.trim();

          posicion = cad.indexOf( cadaux ) + cadaux.length();
        }

        modifyAlignments( ultChanges.getFuente(), 2, posicion );

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
            _alstOriginal.set( cont, _alstOriginal.get( cont + 1 ).toString() );
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
            _alstTranslation.set( cont, _alstTranslation.get( cont + 1 ).toString() );
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
        int[] filasEliminadas = new int [tam];

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
            _alstOriginal.set( cont2, _alstOriginal.get( cont2 - tam ).toString() );
          }

          cont2--;
        }

        _iChangesLB--;
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

    _edLeftSegment.setText( formatText( _vwAlignments.
      getValueAt( _vwAlignments.getSelectedRow(), 1 ).toString() ) );
    _edRightSegment.setText( formatText( _vwAlignments.
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

          _edLeftSegment.setText( formatText( _vwAlignments.getValueAt( fila - 1, 1 ).toString() ) );
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
   *  Funci�n RestaurarTexto. Esta funci�n elimina \n de la frase.
   *
   *  @param cad : la frase a la que se tienen que eliminar los \n
   *  @return cad con la frase
   */
  private String restoreText( final String cad )
  {
    String newCad = "";
    String palabra = "";

    if( cad.length() > _KTAMTEXTAREA )
    {
      final StringTokenizer linea = new StringTokenizer( cad, "\n" );

      while( linea.hasMoreTokens() )
      {
        palabra = linea.nextToken();
        newCad = newCad + " " + palabra;
      }

      newCad = newCad.trim();

      return( newCad );
    }

    return( cad );
  }


  /**
   *  Funci�n FormatearTexto. Esta funci�n formatea el tama�o de la frase al
   *  tama�o de _KTAMTEXTAREA que es una constante con el tama�o del componente
   *  jTextArea.
   *
   *  @param cad : la cadena que hay que formatear
   *  @return cad con la cadena formateada
   */
  private String formatText( final String cad )
  {
    String palabra = "";
    String newCad  = "";
    String frase  = "";

    if( cad.length() > _KTAMTEXTAREA )
    {
      final StringTokenizer linea = new StringTokenizer( cad, " " );

      while( linea.hasMoreTokens() )
      {
        palabra = linea.nextToken();

        if( ( palabra.length() + frase.length() ) < _KTAMTEXTAREA )
        {
          //frase = frase + " ";
          //frase = frase + palabra;
          frase = frase + " " + palabra;
        }
        else
        {
          if( newCad.equals( "" ) ) newCad = frase;
          else newCad = newCad + "\n" + frase;

          frase = "";
          frase = palabra;
        }
      }//  while()

      frase = frase.trim();
      newCad = newCad.trim();
      newCad = newCad + "\n" + frase;

      return( newCad );
    }

    return( cad );
  }


  private void onConfigure()
  {
    //  Regex stuff is not fully implemented/broken
    final Regexp dlgRegexp = new Regexp();

    //dlgConfig.setModal( true );
    dlgRegexp.setVisible( true );

    _strExpReg = dlgRegexp.getRegexp();
    //_KExpReg = dlgConfig.expReg;
    //dlgConfig.dispose();
  }

  private void onCatalan()
  {
    _mniLocaleCatalan.setSelected( true );
    _mniLocaleEnglish.setSelected( false );
    _mniLocaleSpanish.setSelected( false );
    _mniLocaleFrench.setSelected( false );
    _mniLocaleJapanese.setSelected( false );

    _mniLocaleCatalan.setEnabled( false );
    _mniLocaleEnglish.setEnabled( true );
    _mniLocaleSpanish.setEnabled( true );
    _mniLocaleFrench.setEnabled( true );
    _mniLocaleJapanese.setEnabled( true );

    Localization.setLocale( new Locale( "ca" ) );
    updateLocalization();
    updateUI();
  }

  private void onEnglish()
  {
    _mniLocaleCatalan.setSelected( false );
    _mniLocaleEnglish.setSelected( true );
    _mniLocaleSpanish.setSelected( false );
    _mniLocaleFrench.setSelected( false );
    _mniLocaleJapanese.setSelected( false );

    _mniLocaleCatalan.setEnabled( true );
    _mniLocaleEnglish.setEnabled( false );
    _mniLocaleSpanish.setEnabled( true );
    _mniLocaleFrench.setEnabled( true );
    _mniLocaleJapanese.setEnabled( true );

    Localization.setLocale( new Locale( "en" ) );
    updateLocalization();
    updateUI();
  }

  private void onFrench()
  {
    _mniLocaleCatalan.setSelected( false );
    _mniLocaleEnglish.setSelected( false );
    _mniLocaleSpanish.setSelected( false );
    _mniLocaleFrench.setSelected( true );
    _mniLocaleJapanese.setSelected( false );

    _mniLocaleCatalan.setEnabled( true );
    _mniLocaleEnglish.setEnabled( true );
    _mniLocaleSpanish.setEnabled( true );
    _mniLocaleFrench.setEnabled( false );
    _mniLocaleJapanese.setEnabled( true );

    Localization.setLocale( new Locale( "fr" ) );
    updateLocalization();
    updateUI();
  }

  private void onSpanish()
  {
    _mniLocaleCatalan.setSelected( false );
    _mniLocaleEnglish.setSelected( false );
    _mniLocaleSpanish.setSelected( true );
    _mniLocaleFrench.setSelected( false );
    _mniLocaleJapanese.setSelected( false );

    _mniLocaleCatalan.setEnabled( true );
    _mniLocaleEnglish.setEnabled( true );
    _mniLocaleSpanish.setEnabled( false );
    _mniLocaleFrench.setEnabled( true );
    _mniLocaleJapanese.setEnabled( true );

    Localization.setLocale( new Locale( "es", "ES" ) );
    updateLocalization();
    updateUI();
  }

    private void onJapanese()
  {
    _mniLocaleCatalan.setSelected( false );
    _mniLocaleEnglish.setSelected( false );
    _mniLocaleSpanish.setSelected( false );
    _mniLocaleFrench.setSelected( false );
    _mniLocaleJapanese.setSelected( true );

    _mniLocaleCatalan.setEnabled( true );
    _mniLocaleEnglish.setEnabled( true );
    _mniLocaleSpanish.setEnabled( true );
    _mniLocaleFrench.setEnabled( true );
    _mniLocaleJapanese.setEnabled( false );

    Localization.setLocale( new Locale( "ja", "JP" ) );
    updateLocalization();
    updateUI();
  }

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
        col.setHeaderValue( l10n( "TBL.HDR.COL.SOURCE" ) );
      else
        col.setHeaderValue( l10n( "TBL.HDR.COL.SOURCE" ) + _fPathOriginal.getName() );

      col = _vwAlignments.getColumnModel().getColumn( 2 );

      if( _fPathTranslation == null )
        col.setHeaderValue( l10n( "TBL.HDR.COL.TARGET" ) );
      else
        col.setHeaderValue( l10n( "TBL.HDR.COL.TARGET" ) + _fPathTranslation.getName() );

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
    setTitle( l10n( "WND.APP.TITLE" ) );
    _vwControls.updateText();

    _mnuFile       .setText( l10n( "MNU.FILE" ) );
    _mniFileQuit   .setText( l10n( "MNI.FILE.EXIT" ) );

    _mnuHelp       .setText( l10n( "MNU.HELP" ) );
    _mniHelpAbout  .setText( l10n( "MNI.HELP.ABOUT" ) );
    _mniHelpManual  .setText( l10n( "MNI.HELP.MANUAL" ) );

    _mniFileOpen   .setActionCommand( l10n( "MNI.FILE.OPEN" ) );
    _mniFileOpen   .setText( l10n( "MNI.FILE.OPEN" ) );

    _mniFileClose.setText( l10n( "MNI.FILE.ABORT" ) );

    _mnuSettings      .setText( l10n( "MNU.SETTINGS" ) );
    _mniSettingsRegexp.setText( l10n( "MNI.SETTINGS.REGEX" ) );

    _mniFileSaveAs.setText( l10n( "MNI.FILE.SAVEAS" ) );

    _mnuLocale         .setText( l10n( "MNU.LANGUAGE" ) );
    _mniLocaleCatalan  .setText( l10n( "MNI.LANGUAGE.CA" ) );
    _mniLocaleCatalan  .setToolTipText( l10n( "MNI.LANGUAGE.CA.TOOLTIP" ) );
    _mniLocaleEnglish  .setText( l10n( "MNI.LANGUAGE.EN" ) );
    _mniLocaleEnglish  .setToolTipText( l10n( "MNI.LANGUAGE.EN.TOOLTIP" ) );
    _mniLocaleSpanish  .setText( l10n( "MNI.LANGUAGE.ES" ) );
    _mniLocaleSpanish  .setToolTipText( l10n( "MNI.LANGUAGE.ES.TOOLTIP" ) );
    _mniLocaleFrench   .setText( l10n( "MNI.LANGUAGE.FR" ) );
    _mniLocaleFrench   .setToolTipText( l10n( "MNI.LANGUAGE.FR.TOOLTIP" ) );

    _mncbSettingsLinebreak.setText( l10n( "MNI.SETTINGS.LINEBREAK" ) );
    _mncbSettingsLinebreak.setToolTipText( l10n(  "MNI.SETTINGS.LINEBREAK.TOOLTIP" ) );
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

    maxTamArrays = largersizeSegments();

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

    JOptionPane.showMessageDialog( this, l10n( "MSG.ERASED" ) + " " +
      lineasLimpiar + " " + l10n( "MSG.BLANK.ROWS" ) );


    // "Se han borrado n
    // filas en blanco"
    if( lineasLimpiar > 0 )
    {
      _iChangesLB++;
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

    _alstOriginal.add( _alstOriginal.size(), 
      _alstOriginal.get( _alstOriginal.size() - 1 ).toString() );
    _alstTranslation.add( _alstTranslation.size(), 
      _alstTranslation.get( _alstTranslation.size() - 1 ).toString() );

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

  //  ToDo: new alignment functionality from TagAligner lib
  //  Note: When changing this, a major change, provide a separate
  //  method to the new alternative for experiemental purposes.
  //  Select the alignment method desired via a wrapper method possibly
  //
  private boolean align()
  {
    try
    {
      final int tamF   = _alstOriginal.size();
      final int tamM   = _alstTranslation.size();
      final float[] v1          = new float[tamF];
      final float[] v2          = new float[tamM];
      final float[][] votacion  = new float[tamF + 1][tamM + 1];
      final float[][] temporal  = new float[tamF + 1][tamM + 1];
      final float[][] resultado = new float[tamF + 1][tamM + 1];
      float ganancia_x = 5;
      float ganancia_s = 1;
      float ganancia_e = 1;
      int limite_d = 2;
      int cont  = 0;
      int cont2 = 0;
      final float DBL_MAX = 999999999;

      _ult_recorridoinv = "";

      // Inicializar los vectores con el tama�o de cada segmento.
      // Inicializar el vector resultado y votaciones a cero
      //
      // Initialize vectors with the size of each segment
      // Initialize the result vector and votes to zero
      for( cont = 0; cont < tamF; cont++ )
        v1[cont] = _alstOriginal.get( cont ).toString().length();

      for( cont = 0; cont < tamM; cont++ )
        v2[cont] = _alstTranslation.get( cont ).toString().length();

      for( cont = 0; cont <= tamF; cont++ )
      //{
        for( cont2 = 0; cont2 <= tamM; cont2++ )
        {
          votacion[tamF][tamM] = 0;
          resultado[tamF][tamM] = 0;
        }
      //}

      // Inicializar la primera columna y la primera fila de la matriz temporal
      // a ceros.
      // Initialize the first column and the first row of the temporary array
      // with zeros
      for( cont = 0; cont <= tamF; cont++ ) temporal[cont][0] = DBL_MAX;
      for( cont = 0; cont <= tamM; cont++ ) temporal[0][cont] = DBL_MAX;

      temporal[0][0] = 0;

      for( int d = 1; d <= limite_d; d++ )
        for( int i = 1; i <= tamF; i++ )
          for( int j = 1; j <= tamM; j++ )
            temporal[i][j] = cost( temporal, i, j, v1, v2, d );

      // actualizaci�n de la matriz resultado
      // updating the result array
      votacion[tamF][tamM] += ganancia_x;
      int i = tamF;
      int j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( argmin3( temporal[i - 1][j - 1], temporal[i - 1][j],
            temporal[i][j - 1] ) )
        {
          case 1:
          {
            votacion[i - 1][j - 1] += ganancia_x;
            i--;
            j--;
            break;
          }
          case 2:
          {
            votacion[i - 1][j] += ganancia_s;
            i--;
            break;
          }
          case 3:
          {
            votacion[i][j - 1] += ganancia_e;
            j--;
            break;
          }
        }//  switch()
      }//  while()

      // c�lculo del camino con m�xima ganancia
      // computing the maximum-gain path
      for( i = 1; i <= tamF; i++ )
        for( j = 1; j <= tamM; j++ )
          resultado[i][j] = max3( resultado[i - 1][j - 1], resultado[i - 1][j],
              resultado[i][j - 1] )
              + votacion[i][j];

      i = tamF;
      j = tamM;

      while( i > 1 && j > 1 )
      {
        switch( argmax3( resultado[i - 1][j - 1], resultado[i - 1][j],
            resultado[i][j - 1] ) )
        {
          case 1:
          {
            _ult_recorridoinv += 'x';
            j--;
            i--;
            break;
          }
          case 2:
          {
            _ult_recorridoinv += 's';
            i--;
            break;
          }
          case 3:
          {
            _ult_recorridoinv += 'e';
            j--;
            break;
          }
        }//  switch()
      }//  while()

      // simplificaci�n de _ult_recorridoinv
      // simplification of _ult_recorridoinv
      char estado = 'x';
      String almacenamiento = "";
      i = tamF - 1;
      j = tamM - 1;

      for( cont = 0; cont < _ult_recorridoinv.length(); cont++ )
      {
        switch( _ult_recorridoinv.charAt( cont ) )
        {
          case 's':
          {
            i--;

            if( estado == 'e' && isAlignedOKSE( v1, v2, i, j ) )
            {
              char[] almchar = almacenamiento.toCharArray();
              almchar[almacenamiento.length() - 1] = 'x';
              almacenamiento = new String( almchar );
              estado = 'x';
            }
            else
            {
              almacenamiento = almacenamiento + 's';
              estado = 's';
            }

            break;
          }
          case 'e':
          {
            j--;

            if( estado == 's' && isAlignedOKES( v1, v2, i, j ) )
            {
              char[] almchar = almacenamiento.toCharArray();
              almchar[almacenamiento.length() - 1] = 'x';
              almacenamiento = new String( almchar );
              estado = 'x';
            }
            else
            {
              almacenamiento = almacenamiento + 'e';
              estado = 'e';
            }

            break;
          }
          case 'x':
          {
            i--;
            estado = 'x';
            almacenamiento = almacenamiento + 'x';

            break;
          }
        }//  switch()
      }//  for()

      _ult_recorridoinv = almacenamiento;

      int f1 = 1;
      int f2 = 1;
      final ArrayList Fuente = new ArrayList();
      final ArrayList Meta   = new ArrayList();

      Fuente.add( _alstOriginal.get( 0 ) );
      Meta.add( _alstTranslation.get( 0 ) );

      for( i = _ult_recorridoinv.length() - 1; i >= 0; i-- )
      {
        switch( _ult_recorridoinv.charAt( i ) )
        {
          case 'x':
          {
            Fuente.add( _alstOriginal.get( f1 ) );
            Meta.add( _alstTranslation.get( f2 ) );
            f1++;
            f2++;

            break;
          }
          case 's':
          {
            Fuente.add( _alstOriginal.get( f1 ) );
            Meta.add( "" );
            f1++;

            break;
          }
          case 'e':
          {
            Fuente.add( "" );
            Meta.add( _alstTranslation.get( f2 ) );
            f2++;

            break;
          }
        }//  switch()
      }//  for()

      while( !_alstOriginal.isEmpty() )    _alstOriginal.remove( 0 );
      while( !_alstTranslation.isEmpty() ) _alstTranslation.remove( 0 );

      for( cont = 0; cont < Fuente.size(); cont++ )
        _alstOriginal.add( Fuente.get( cont ) );

      for( cont = 0; cont < Meta.size(); cont++ )
        _alstTranslation.add( Meta.get( cont ) );

      return( true );
    }

    //  FixMe: this should never happen if the program is designed properly
    //  It is very bad practice to have to catch OutOfMemoryError inside
    //  an app like this. A little pre-calculation/estimate of required memory
    //  from file sizes or related could subvert this altogether.
    catch( final java.lang.OutOfMemoryError ex )
    {
      //JOptionPane.showMessageDialog( _pnl, l10n( "MSG.MEMORY.INSUFFICIENT" ),
      JOptionPane.showMessageDialog( this, l10n( "MSG.MEMORY.INSUFFICIENT" ),
        l10n( "MSG.ERROR"), JOptionPane.ERROR_MESSAGE );

      return( false );
    }
  }

  private float cost( final float[][] mat, final int i, final int j, final float[] v1, final float[] v2, final int d )
  {
    final float b2 = Math.abs( v1[i - 1] - v2[j - 1] );

    return( min3( mat[i - 1][j] + v1[i - 1] / d, mat[i - 1][j - 1] + b2,
        mat[i][j - 1] + v2[j - 1] / d ) );
  }

  private float min3( float a, final float b, final float c )
  {
    if( b < a ) { a = b; }

    if( c < a ) return( c );

    return( a );
  }

  private float max3( float a, final float b, final float c )
  {
    if( b > a ) { a = b; }

    if( c > a ) return( c );

    return( a );
  }

  private int argmin3( float a, final float b, final float c )
  {
    int iArgMin3 = 0;

    if( b < a )
    {
      a = b;
      ++iArgMin3;  // +1
    }

    if( c < a ) return( 3 );

    return( ++iArgMin3 );  // 1 or 2
  }

  private int argmax3( float a, final float b, final float c )
  {
    int iArgMax3 = 0;

    if( b > a )
    {
      a = b;
      ++iArgMax3;  // +1
    }

    if( c > a ) return( 3 );

    return( ++iArgMax3 );  // 1 or 2
  }

  private boolean isAlignedOKES( final float[] v1, final float[] v2, final int i,    final int j )
  {
    return( Math.abs( v1[i - 1] - v2[j] ) < Math.abs( v1[i] - v2[j] )
      ? true : false );
  }

  private boolean isAlignedOKSE( final float[] v1, final float[] v2, final int i, final int j )
  {
    return( Math.abs( v1[i] - v2[j - 1] ) < Math.abs( v1[i] - v2[j] )
      ? true : false );
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
   *  @return void
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
   *  @param  void
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
   *  @param  Font
   *  @return void
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
      final String strFontName  = "Dialog";
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
   *  @param  Font
   *  @return void
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
    _mniSettingsRegexp      .setFont( font );
    _mncbSettingsLinebreak  .setFont( font );

    //  Language sub-menu
    _mnuLocale         .setFont( font );
    _mniLocaleCatalan  .setFont( font );  //  ca
    _mniLocaleEnglish  .setFont( font );  //  en
    _mniLocaleSpanish  .setFont( font );  //  es
    _mniLocaleFrench   .setFont( font );  //  fr

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
   *  @param  void
   *  @return Font
   */
  final public Font getUserInterfaceFont() { return( _fntUserInterface ); }


  /**
   *  Table header font mutator
   *
   *  @param  Font
   *  @return void
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
   *  @param  void
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
   *  @param  void
   *  @return Font
   */
  final public Font getSourceEditorFont() { return( _fntSourceEditor ); }


  /**
   *  Translation editor font mutator
   *
   *  @param  Font
   *  @return void
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
   *  @param  void
   *  @return Font
   */
  final public Font getTargetEditorFont() { return( _fntTargetEditor ); }

  /**
   *  Font family names accessor
   *
   *  @param  void
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
   *  @param  Font
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
   *  @param  String
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
  final public void windowActivated( final WindowEvent evt ) {}
  final public void windowClosed( final WindowEvent evt )    {}

  final public void windowClosing( final WindowEvent evt )
  { if( evt.getSource() == this ) quit(); }

  final public void windowDeactivated( final WindowEvent evt ) {}
  final public void windowDeiconified( final WindowEvent evt ) {}
  final public void windowIconified( final WindowEvent evt )   {}
  final public void windowOpened( final WindowEvent evt )      {}

  /**
   *  class action listener implementation
   *
   *  @param  ActionEvent
   *  @return void
   */
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

      else if( actor == _mniSettingsRegexp ) onConfigure();
      else if( actor == _mniSettingsFonts )  displayFontSelector();

      else if( actor == _mniLocaleCatalan )  onCatalan();
      else if( actor == _mniLocaleEnglish )  onEnglish();
      else if( actor == _mniLocaleSpanish )  onSpanish();
      else if( actor == _mniLocaleFrench )   onFrench();

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
        { System.out.println( l10n( "OTP.LNF.INIT.ERROR" ) ); }
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
        { System.out.println( l10n( "OTP.LNF.INIT.ERROR" ) ); }
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
        { System.out.println( l10n( "OTP.LNF.INIT.ERROR" ) ); }
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
        { System.out.println( l10n( "OTP.LNF.INIT.ERROR" ) ); }
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
        { System.out.println( l10n( "OTP.LNF.INIT.ERROR" ) ); }
      }

      else if( actor == _mniHelpManual )  displayManual();
      else if( actor == _mniHelpAbout )   displayAbout();
    }
  }

}// Bitext2tmxWindow{}


