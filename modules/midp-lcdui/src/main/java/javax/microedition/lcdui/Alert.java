// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package javax.microedition.lcdui;

public class Alert
	extends Screen
{
	/**
	 * This is delivered to a listener to specify that the alert has been
	 * dismissed.
	 */
	public static final Command DISMISS_COMMAND =
		new Command("Okay", Command.OK, 0, true);
	
	/** Specifies that the alert should last forever. */
	public static final int FOREVER =
		-2;
	
	/** The message to display. */
	volatile String _message;
	
	/** The image to use. */
	volatile Image _image;
	
	/** The type of alert this is. */
	volatile AlertType _type;
	
	/** The duration the alert should last in milliseconds. */
	volatile int _timeout = Alert.FOREVER;
	
	/**
	 * Initializes the alert with just a title.
	 *
	 * @param __title The title of the alert, may be {@code null}.
	 * @since 2017/02/28
	 */
	public Alert(String __title)
	{
		this(__title, null, null, null);
	}
	
	/**
	 * Initializes the alert with just a title, message, image, and type.
	 *
	 * @param __title The title of the alert, may be {@code null}.
	 * @param __message The message to show in the alert, may be {@code null}.
	 * @param __icon The icon to display, may be {@code null}.
	 * @param __type The type of this alert, may be {@code null}.
	 * @since 2017/02/28
	 */
	public Alert(String __title, String __message, Image __icon,
		AlertType __type)
	{
		this._message = __message;
		this._image = __icon;
		this._type = __type;
		
		// Set titles
		this._userTitle = __title;
		if (__title != null)
			this._displayTitle = __title;
	}
	
	@Override
	public void addCommand(Command __a)
	{
		throw new todo.TODO();
	}
	
	public int getDefaultTimeout()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/24
	 */
	@Override
	public int getHeight()
	{
		throw new todo.TODO();
		/*
		return this.__defaultHeight();
		*/
	}
	
	public Image getImage()
	{
		throw new todo.TODO();
	}
	
	public Gauge getIndicator()
	{
		throw new todo.TODO();
	}
	
	public String getString()
	{
		throw new todo.TODO();
	}
	
	public int getTimeout()
	{
		throw new todo.TODO();
	}
	
	public AlertType getType()
	{
		throw new todo.TODO();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2017/05/24
	 */
	@Override
	public int getWidth()
	{
		throw new todo.TODO();
		/*
		return this.__defaultWidth();
		*/
	}
	
	@Override
	public void removeCommand(Command __a)
	{
		throw new todo.TODO();
	}
	
	@Override
	public void setCommandListener(CommandListener __a)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Sets the image to be displayed for this alert. If the image is mutable
	 * then this will take a snapshot of the image and use that snapshot
	 * instead of the normal image.
	 *
	 * A new snapshot from a mutable image can be created by performing:
	 * {@code alert.setImage(alert.getImage())}.
	 *
	 * @param __i The image to set or {@code null} to clear it.
	 * @since 2018/04/06
	 */
	public void setImage(Image __i)
	{
		throw new todo.TODO();
		/*
		Image clone = (__i != null && __i.isMutable() ?
			Image.createImage(__i) : __i);
		LcdServiceCall.voidCall(LcdFunction.SET_IMAGE, this._handle,
			(__i == null ? -1 : __i._handle),
			(clone == null ? -1 : clone._handle));
		this._image = __i;
		*/
	}
	
	public void setIndicator(Gauge __a)
	{
		throw new todo.TODO();
	}
	
	/**
	 * Sets the message which is used in the alert.
	 *
	 * @param __s The message to use.
	 * @since 2017/02/28
	 */
	public void setString(String __s)
	{
		throw new todo.TODO();
		/*
		this._message = __s;
		
		todo.DEBUG.note("Set alert message: %s%n", __s);
		*/
	}
	
	/**
	 * Sets the duration for which the alert should last.
	 *
	 * @param __ms The number of milliseconds to display the message for or
	 * {@link #FOREVER}.
	 * @throws IllegalArgumentException If the duration is not positive and is
	 * not {@link #FOREVER}.
	 * @since 2017/02/28
	 */
	public void setTimeout(int __ms)
		throws IllegalArgumentException
	{
		// {@squirreljme.error EB19 The specified number of milliseconds is
		// negative. (The number of milliseconds specified)}
		if (__ms < 0 && __ms != Alert.FOREVER)
			throw new IllegalArgumentException(String.format("EB19 %d", __ms));
		
		// Set
		this._timeout = __ms;
	}
	
	/**
	 * Sets the type of this alert.
	 *
	 * @param __t The alert type, may be {@code null}.
	 * @since 2017/02/28
	 */
	public void setType(AlertType __t)
	{
		throw new todo.TODO();
		/*
		this._type = __t;
		*/
	}
}


