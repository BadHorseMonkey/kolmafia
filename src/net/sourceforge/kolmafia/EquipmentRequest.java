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

import java.util.List;
import java.util.StringTokenizer;

/**
 * An extension of <code>KoLRequest</code> which retrieves a list of
 * the character's equipment from the server.  At the current time,
 * there is no support for actually equipping items, so only the items
 * which are currently equipped are retrieved.
 */

public class EquipmentRequest extends KoLRequest
{
	private KoLCharacter character;

	/**
	 * Constructs a new <code>EquipmentRequest</code>, overwriting the
	 * data located in the provided character.
	 *
	 * @param	client	The client to be notified in the event of an error
	 * @param	character	The character to which this will record the retrieved equipment
	 */

	public EquipmentRequest( KoLmafia client )
	{
		// The only thing to do is to retrieve the page from
		// the client - all variable initialization comes from
		// when the request is actually run.

		super( client, "inventory.php" );
		this.character = client.getCharacterData();

		addFormField( "which", "2" );
	}

	/**
	 * Executes the <code>EquipmentRequest</code>.  Note that at the current
	 * time, only the character's currently equipped items and familiar item
	 * will be stored.
	 */

	public void run()
	{
		super.run();

		// If an error state occurred, return from this
		// request, since there's no content to parse

		if ( isErrorState || responseCode != 200 )
			return;

		// The easiest way to retrieve the character sheet
		// data is to first strip all of the HTML from the
		// reply, and then tokenize on the stripped-down
		// version.  This can be done through simple regular
		// expression matching.

		String plainTextContent = replyContent.replaceAll( "<.*?>", "\n" );
		StringTokenizer parsedContent = new StringTokenizer( plainTextContent, "\n" );

		logStream.println( "Parsing equipment data..." );

		try
		{
			while ( !parsedContent.nextToken().startsWith( "Hat:" ) );
			String hat = parsedContent.nextToken();

			while ( !parsedContent.nextToken().startsWith( "Weapon:" ) );
			String weapon = parsedContent.nextToken();

			while ( !parsedContent.nextToken().startsWith( "Pants:" ) );
			String pants = parsedContent.nextToken();

			String [] accessories = new String[3];
			for ( int i = 0; i < 3; ++i )
				accessories[i] = "none";
			String familiarItem = "none";

			int accessoryCount = 0;
			String lastToken;

			do
			{
				lastToken = parsedContent.nextToken();

				if ( lastToken.startsWith( "Accessory:" ) )
					accessories[ accessoryCount++ ] = parsedContent.nextToken();
				else if ( lastToken.startsWith( "Familiar:" ) )
					familiarItem = parsedContent.nextToken();
			}
			while ( !lastToken.startsWith( "Outfits:" ) );

			character.setEquipment( hat, weapon, pants, accessories[0], accessories[1], accessories[2], familiarItem );

			// Now that the equipped items have been parsed, begin parsing for the items
			// which are not currently equipped, but are listed in the equipment page.
			// Normally, custom outfits would be parsed; for now, this part is skipped.

			while ( !lastToken.equals( "Inventory:" ) )
				lastToken = parsedContent.nextToken();
			lastToken = parsedContent.nextToken();

			// It's possible that there are other options available (such as cooking,
			// cocktailing, combining and the like); these options should be skipped
			// as well.

			while ( lastToken.startsWith( "[" ) || lastToken.startsWith( "&" ) )
				lastToken = parsedContent.nextToken();

			// Now you have the actual items; these can be added to the character's
			// inventory by parsing the item, skipping the next three tokens, and
			// continuing this while you still have tokens.

			List inventory = client.getInventory();

			if ( parsedContent.countTokens() > 1 )
			{
				do
				{
					try
					{
						AdventureResult result = AdventureResult.parseResult( lastToken );

						// Make sure to only add the result if it exists
						// in the item database; otherwise, it could cause
						// problems when you're moving items around

						if ( TradeableItemDatabase.contains( result.getResultName() ) )
							AdventureResult.addResultToList( inventory, result );

						skipTokens( parsedContent, 3 );

						if ( parsedContent.hasMoreTokens() )
							lastToken = parsedContent.nextToken();
					}
					catch ( Exception e )
					{
						// If an exception occurs during the parsing, just
						// continue after notifying the LogStream of the
						// error.  This could be handled better, but not now.

						logStream.println( e );

						while ( parsedContent.hasMoreTokens() )
							parsedContent.nextToken();
					}
				}
				while ( parsedContent.hasMoreTokens() );
			}

			logStream.println( "Parsing complete." );
		}
		catch ( RuntimeException e )
		{
			logStream.println( e );
		}
	}
}