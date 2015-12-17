/*
#######################################################################
#
#  bitext2tmx - Bitext Aligner/TMX Editor
#
#  Copyright (C) 2008-2009  Raymond: Martin
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
#  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
#
#######################################################################
*/


package bitext2tmx.util.gui;

import java.awt.Image;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static bitext2tmx.util.Utilities.isMacOSX;


final public class AquaAdapter implements InvocationHandler
{
  static Object _objAquaApp;

  final protected Object _objReceiver;
  final protected Method _mtdHandler;
  final protected String _strAquaEvent;

  //private static Frame invisibleFrame;

  public static enum AquaEvent { ABOUT, OPEN, PREFERENCES, QUIT };

  protected final static String[] _straAquaEvents =
  {
    "handleAbout",
    "handleOpenFile",
    "handlePreferences",
    "handleQuit"
  };

  protected AquaAdapter( final Object objReceiver, final Method mtdHandler,
    final String strAquaEvent )
  {
    _objReceiver  = objReceiver;
    _mtdHandler   = mtdHandler;
    _strAquaEvent = strAquaEvent;
  }


  final public static void connect( final Object objReceiver, final String strHandler,
    final AquaEvent evtAqua )
  {
    if( !isMacOSX() ) return;

    if( objReceiver == null || strHandler == null || evtAqua == null )
      return;

    try
    {
      final Method mtdHandler;

      mtdHandler = objReceiver.getClass().
        getDeclaredMethod( strHandler, (Class[])null );

      final Class clsApplication = Class.forName( "com.apple.eawt.Application" );

      if( _objAquaApp == null )
        _objAquaApp = clsApplication.
          getConstructor( (Class[])null ).newInstance( (Object[])null );

      final Class clsApplicationListener = Class.forName( "com.apple.eawt.ApplicationListener" );

      final Object objProxy = Proxy.
        newProxyInstance( AquaAdapter.class.getClassLoader(),
          new Class[] { clsApplicationListener }, new AquaAdapter( objReceiver,
            mtdHandler, _straAquaEvents[evtAqua.ordinal()] ) );

      final Method mtdSender = clsApplication.
        getDeclaredMethod( "addApplicationListener",
          new Class[] { clsApplicationListener } );
      final Object result = mtdSender.invoke( _objAquaApp, new Object[] { objProxy } );
    }
    catch( final ClassNotFoundException cnfe )
    {
      System.err.println( "Mac OS X version does not support Apple EAWT" );
      System.err.println( "ApplicationEvent handling is disabled: " + cnfe );
    }
    catch( final Exception e )
    {
      System.err.println( "AquaAdapter could not communicate with EAWT:" );
      e.printStackTrace();
    }

    if( evtAqua == AquaEvent.PREFERENCES )
      try
      {
        final Method mtdEnablePrefs = _objAquaApp.getClass().
          getDeclaredMethod( "setEnabledPreferencesMenu",
            new Class[] { boolean.class } );
        mtdEnablePrefs.invoke( _objAquaApp, new Object[] { Boolean.valueOf( true ) } );
      }
      catch( final Exception e )
      {
        System.err.println( "AquaAdapter could not enable Preferences");
        e.printStackTrace();
      }
  }


  final public Object invoke( final Object objProxy, final Method mtdAqua,
    final Object[] objaArgs ) throws Throwable
  {
    if( _mtdHandler != null && objaArgs.length == 1 &&
        _strAquaEvent.equals( mtdAqua.getName() ) && objaArgs[0] != null )
      try
      {
        //  setHandled must be called to inform the Aqua side of the
        //  expected behavior, else it will go default
        final Method mtdSetHandled = objaArgs[0].getClass().
          getDeclaredMethod( "setHandled", new Class[] { boolean.class } );
        mtdSetHandled.invoke( objaArgs[0],
          new Object[] { Boolean.valueOf( fireHandler( objaArgs[0] ) ) } );
      }
      catch( final Exception e )
      {
        System.err.println( "AquaAdapter was unable to handle ApplicationEvent: " +
          objaArgs[0] );
        e.printStackTrace();
      }

    return( null );
  }

  final public boolean fireHandler( Object objEvent )
    throws InvocationTargetException, IllegalAccessException
  {
    final Object objResult = _mtdHandler.invoke( _objReceiver, (Object[])null );

    //  Default return value is true, this will cause whatever default
    //  behavior is associated on the Aqua side - app close in the case
    //  of firing off a quit event
    return( objResult == null ? true :
      Boolean.valueOf( objResult.toString() ).booleanValue() );
  }

  final public static void setDockIconImage( final Image image )
  {
    if( !isMacOSX() || image == null ) return;

    try
    {
      final Class clsApplication = Class.forName( "com.apple.eawt.Application" );

      if( _objAquaApp == null )
        _objAquaApp = clsApplication.
          getConstructor( (Class[])null ).newInstance( (Object[])null );

      final Method mtdSetDockIconImage = _objAquaApp.getClass().
        getDeclaredMethod( "setDockIconImage",
          new Class[] { Image.class } );

      mtdSetDockIconImage.invoke( _objAquaApp, new Object[] { image } );
    }
    catch( final ClassNotFoundException cnfe )
    {
      System.err.println( "Mac OS X version does not support Apple EAWT" );
      System.err.println( "ApplicationEvent handling is disabled: " + cnfe );
    }
    catch( final Exception e )
    {
      System.err.println( "AquaAdapter could not communicate with EAWT:" );
      e.printStackTrace();
    }
  }

/*
  final public static boolean isSwingUsingScreenMenuBar()
  {
    boolean result  = false;
    final LookAndFeel laf = UIManager.getLookAndFeel();
    final String id       = laf.getID();

    if( id.equals( "Mac" ) || id.equals( "Aqua" ) ) result = true;

    return( result );
  }
*/

/*
  final public static void setFramelessJMenuBar( final JMenuBar menuBar )
  {
    final boolean usingScreenMenuBar = isSwingUsingScreenMenuBar();

    if( invisibleFrame == null )
    {
      invisibleFrame = new JFrame();
      ( (JFrame)invisibleFrame ).
        setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );

      try
      { invisibleFrame.setUndecorated( true ); }
      catch( final Exception ex ) {}

      invisibleFrame.setLocation( 10000, 10000 );
      invisibleFrame.setSize( 0, 0 );
    }

    if( usingScreenMenuBar)
      if( !invisibleFrame.isVisible() ) invisibleFrame.setVisible( true );

    ((JFrame)invisibleFrame).setJMenuBar( menuBar );
  }
*/

}


