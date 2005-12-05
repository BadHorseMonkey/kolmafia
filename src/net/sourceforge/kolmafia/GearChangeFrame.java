/**
 * Copyright (c) 2005, KoLmafia development team
 * http://kolmafia.sourceforge.net/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  [1] Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *  [2] Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in
 *      the documentation and/or other materials provided with the
 *      distribution.
 *  [3] Neither the name "KoLmafia development team" nor the names of
 *      its contributors may be used to endorse or promote products
 *      derived from this software without specific prior written
 *      permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package net.sourceforge.kolmafia;

// layout
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.BorderLayout;

// containers
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

// event listeners
import java.awt.event.KeyAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.Constructor;

// utilities
import net.java.dev.spellcast.utilities.JComponentUtilities;
import net.java.dev.spellcast.utilities.LockableListModel;

/**
 * An extension of <code>KoLFrame</code> used to display the character
 * sheet for the current user.  Note that this can only be instantiated
 * when the character is logged in; if the character has logged out,
 * this method will contain blank data.  Note also that the avatar that
 * is currently displayed will be the default avatar from the class and
 * will not reflect outfits or customizations.
 */

public class GearChangeFrame extends KoLFrame
{
	private boolean isChanging = false;
	private JComboBox [] equipment;
	private JComboBox outfitSelect, familiarSelect;

	/**
	 * Constructs a new character sheet, using the data located
	 * in the provided session.
	 *
	 * @param	client	The client containing the data associated with the character
	 */

	public GearChangeFrame( KoLmafia client )
	{
		super( client, "Changing Gears" );

		// For now, because character listeners haven't been implemented
		// yet, re-request the character sheet from the server

		setResizable( false );
		contentPanel = null;

		CardLayout cards = new CardLayout( 10, 10 );
		getContentPane().setLayout( cards );

		getContentPane().add( createEquipPanel(), "" );
		refreshEquipPanel();
		addMenuBar();
	}

	private void addMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar( menuBar );

		JMenu refreshMenu = new JMenu( "Refresh" );
		menuBar.add( refreshMenu );

		refreshMenu.add( new RefreshMenuItem( "Equipment", new EquipmentRequest( client, EquipmentRequest.EQUIPMENT ) ) );
		refreshMenu.add( new RefreshMenuItem( "Familiars", new FamiliarRequest( client ) ) );

		addOptionsMenu( menuBar );
		addHelpMenu( menuBar );
	}

	/**
	 * Sets all of the internal panels to a disabled or enabled state; this
	 * prevents the user from modifying the data as it's getting sent, leading
	 * to uncertainty and generally bad things.
	 */

	public void setEnabled( boolean isEnabled )
	{
		if ( equipment != null )
			for ( int i = 0; i < equipment.length; ++i )
				equipment[i].setEnabled( isEnabled );

		if ( outfitSelect != null )
			outfitSelect.setEnabled( isEnabled );

		if ( familiarSelect != null )
			familiarSelect.setEnabled( isEnabled );
	}

	/**
	 * Utility method for creating a panel displaying the character's current
	 * equipment, accessories and familiar item.
	 *
	 * @return	a <code>JPanel</code> displaying the character's equipment
	 */

	private JPanel createEquipPanel()
	{
		JPanel fieldPanel = new JPanel();
		fieldPanel.setLayout( new GridLayout( 15, 1 ) );

		fieldPanel.add( new JLabel( " " ) );
		fieldPanel.add( new JLabel( "Hat:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Weapon:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Shirt:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Pants:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( " " ) );
		fieldPanel.add( new JLabel( "Accessory:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Accessory:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Accessory:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( " " ) );
		fieldPanel.add( new JLabel( "Familiar:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( "Item:  ", JLabel.RIGHT ) );
		fieldPanel.add( new JLabel( " " ) );
		fieldPanel.add( new JLabel( "Outfit:  ", JLabel.RIGHT ) );

		JPanel valuePanel = new JPanel();
		valuePanel.setLayout( new GridLayout( 15, 1 ) );

		valuePanel.add( new JLabel( " " ) );

		equipment = new JComboBox[8];
		LockableListModel [] equipmentLists = KoLCharacter.getEquipmentLists();

		for ( int i = 0; i < 4; ++i )
		{
			equipment[i] = new ChangeComboBox( equipmentLists[i], EquipmentRequest.class, String.class, new Integer(i) );
			JComponentUtilities.setComponentSize( equipment[i], 300, 20 );
			valuePanel.add( equipment[i] );
		}

		valuePanel.add( new JLabel( " " ) );

		for ( int i = 4; i < 7; ++i )
		{
			equipment[i] = new ChangeComboBox( equipmentLists[i], EquipmentRequest.class, String.class, new Integer(i) );
			JComponentUtilities.setComponentSize( equipment[i], 300, 20 );
			valuePanel.add( equipment[i] );
		}

		valuePanel.add( new JLabel( " " ) );

		familiarSelect = new ChangeComboBox( KoLCharacter.getFamiliarList(), FamiliarRequest.class, FamiliarData.class );
		JComponentUtilities.setComponentSize( familiarSelect, 300, 20 );
		valuePanel.add( familiarSelect );

		equipment[7] = new ChangeComboBox( equipmentLists[7], EquipmentRequest.class, String.class, new Integer(7) );
		JComponentUtilities.setComponentSize( equipment[7], 300, 20 );
		valuePanel.add( equipment[7] );

		valuePanel.add( new JLabel( " " ) );

		outfitSelect = new ChangeComboBox( KoLCharacter.getOutfits(), EquipmentRequest.class, SpecialOutfit.class );
		JComponentUtilities.setComponentSize( outfitSelect, 300, 20 );
		valuePanel.add( outfitSelect );

		JPanel equipPanel = new JPanel();
		equipPanel.setLayout( new BorderLayout() );
		equipPanel.add( fieldPanel, BorderLayout.WEST );
		equipPanel.add( valuePanel, BorderLayout.EAST );

		return equipPanel;
	}

	private void refreshEquipPanel()
	{
		setEnabled( false );
		outfitSelect.setSelectedItem( null );
		KoLCharacter.updateEquipmentLists();
		setEnabled( true );
	}

	private class RefreshMenuItem extends JMenuItem implements ActionListener, Runnable
	{
		private KoLRequest request;

		public RefreshMenuItem( String title, KoLRequest request )
		{
			super( title );
			addActionListener( this );

			this.request = request;
		}

		public void actionPerformed( ActionEvent e )
		{	(new DaemonThread( this )).start();
		}

		public void run()
		{
			GearChangeFrame.this.setEnabled( false );

			if ( request instanceof FamiliarRequest )
				familiarSelect.setSelectedItem( null );
			else
			{
				for ( int i = 0; i < equipment.length; ++i )
					equipment[i].setSelectedItem( null );
			}

			request.run();
			refreshEquipPanel();
		}
	}

	private class ChangeComboBox extends JComboBox implements Runnable
	{
		private Integer slot;
		private Object [] parameters;
		private Constructor constructor;

		public ChangeComboBox( LockableListModel selector, Class requestClass, Class parameterClass )
		{
			super( selector );
			addActionListener( this );

			Class [] parameterTypes = new Class[2];
			parameterTypes[0] = KoLmafia.class;
			parameterTypes[1] = parameterClass;

			initialize( requestClass, parameterTypes );
			this.slot = null;
		}

		public ChangeComboBox( LockableListModel selector, Class requestClass, Class parameterClass, Integer slot )
		{
			super( selector );
			addActionListener( this );

			Class [] parameterTypes = new Class[3];
			parameterTypes[0] = KoLmafia.class;
			parameterTypes[1] = parameterClass;
			parameterTypes[2] = Integer.class;

			initialize( requestClass, parameterTypes );
			this.parameters[2] = slot;
			this.slot = slot;
		}

		private void initialize( Class requestClass, Class [] parameterTypes )
		{
			try
			{
				this.constructor = requestClass.getConstructor( parameterTypes );
			}
			catch ( Exception e )
			{
				KoLmafia.getLogStream().println( e );
				e.printStackTrace( KoLmafia.getLogStream() );
			}

			this.parameters = new Object[ parameterTypes.length ];
			this.parameters[0] = client;
			for ( int i = 1; i < parameters.length; ++i )
				this.parameters[i] = null;
		}

		public synchronized void actionPerformed( ActionEvent e )
		{
			// Ignore the event if the window is currently not
			// showing, you're in the middle of changing items,
			// or the frame is currently disabled.

			if ( !isShowing() || isChanging || !isEnabled() )
				return;

			if ( e.paramString().endsWith( "=" ) )
				return;

			// Once all the tests above fail, that means the
			// change can be tested.

			executeChange();
		}

		public synchronized void firePopupMenuWillBecomeInvisible()
		{
			super.firePopupMenuWillBecomeInvisible();

			if ( !isShowing() || isChanging || !isEnabled() )
				return;

			executeChange();
		}

		public synchronized void executeChange()
		{
			parameters[1] = getSelectedItem();

			if ( parameters[1] == null )
				return;

			// In order to avoid constant misfiring of the table,
			// make sure that the change thread is not started
			// unless your current equipment does not match the
			// selected equipment.

			if ( ( slot != null && parameters[1].equals( KoLCharacter.getEquipment( slot.intValue() ) ) ) ||
			     ( this == familiarSelect && parameters[1].equals( KoLCharacter.getFamiliar() ) ) ||
			     ( this == outfitSelect && !( parameters[1] instanceof SpecialOutfit ) ) )
				return;

			isChanging = true;
			(new DaemonThread( this )).start();
		}

		public void run()
		{
			try
			{
				GearChangeFrame.this.setEnabled( false );
				client.makeRequest( (Runnable) constructor.newInstance( parameters ), 1 );

				refreshEquipPanel();
				isChanging = false;
			}
			catch ( Exception e )
			{
				setEnabled( false );
				refreshEquipPanel();
			}
		}
	}

	/**
	 * The main method used in the event of testing the way the
	 * user interface looks.  This allows the UI to be tested
	 * without having to constantly log in and out of KoL.
	 */

	public static void main( String [] args )
	{
		Object [] parameters = new Object[1];
		parameters[0] = null;

		(new CreateFrameRunnable( GearChangeFrame.class, parameters )).run();
	}
}
