// -*- Mode: Java; indent-tabs-mode: t; tab-width: 4 -*-
// ---------------------------------------------------------------------------
// Multi-Phasic Applications: SquirrelJME
//     Copyright (C) Stephanie Gawroriski <xer@multiphasicapps.net>
//     Copyright (C) Multi-Phasic Applications <multiphasicapps.net>
// ---------------------------------------------------------------------------
// SquirrelJME is under the GNU General Public License v3+, or later.
// See license.mkd for licensing and copyright information.
// ---------------------------------------------------------------------------

package net.multiphasicapps.javac.basic;

import java.io.Closeable;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import net.multiphasicapps.classfile.BinaryName;
import net.multiphasicapps.javac.FileNameLineAndColumn;
import net.multiphasicapps.javac.token.Token;
import net.multiphasicapps.javac.token.Tokenizer;
import net.multiphasicapps.javac.token.TokenType;

/**
 * This class parses input tokens from the tokenizer and creates the basic
 * structure of the class file.
 *
 * @since 2018/03/12
 */
public final class BasicStructureParser
	implements Closeable, FileNameLineAndColumn
{
	/** The builder which is used to store partial state. */
	protected final BasicStructureBuilder builder =
		new BasicStructureBuilder();
	
	/** The layer to source tokens from. */
	protected final TokenizerLayer layer;
	
	/** Parser states which store the needed temporary data. */
	private final Deque<__State__> _states =
		new ArrayDeque<>();
	
	/**
	 * Initializes the base state.
	 *
	 * @since 2018/03/13
	 */
	{
		this._states.addLast(new __StatePackage__());
	}
	
	/**
	 * Parses the given input stream for class structure data.
	 *
	 * @param __s The name of the file.
	 * @param __in The input stream to read from.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/19
	 */
	public BasicStructureParser(String __s, InputStream __in)
		throws NullPointerException
	{
		this(new Tokenizer(__s, __in));
	}
	
	/**
	 * Parses the given reader for class structure data.
	 *
	 * @param __s The name of the file.
	 * @param __in The input stream to read from.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/19
	 */
	public BasicStructureParser(String __s, Reader __in)
		throws NullPointerException
	{
		this(new Tokenizer(__s, __in));
	}
	
	/**
	 * Parses the given basic structure from the given tokenizer.
	 *
	 * @param __t The tokenizer to parse from.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/12
	 */
	public BasicStructureParser(Tokenizer __t)
		throws NullPointerException
	{
		if (__t == null)
			throw new NullPointerException("NARG");
		
		// Initialize the parser layer
		this.layer = new TokenizerLayer(__t);
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/12
	 */
	@Override
	public final void close()
		throws IOException
	{
		this.layer.close();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/12
	 */
	@Override
	public final int column()
	{
		return this.layer.column();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/12
	 */
	@Override
	public final String fileName()
	{
		return this.layer.fileName();
	}
	
	/**
	 * {@inheritDoc}
	 * @since 2018/03/12
	 */
	@Override
	public final int line()
	{
		return this.layer.line();
	}
	
	/**
	 * Parses the input source file and builds the structure from it.
	 *
	 * @return The resulting structure.
	 * @throws BasicStructureException If the structure could not be parsed.
	 * @throws IOException On read errors.
	 * @since 2018/03/12
	 */
	public final BasicStructure parse()
		throws BasicStructureException, IOException
	{
		Deque<__State__> states = this._states;
		
		// Parsing is done in a loop
		for (boolean goteof = false; !goteof;)
		{
			__State__ state = states.getLast();
			
			// Depends on the area
			__State__.Area area = state.area;
			switch (area)
			{
					// End of file
				case END_OF_FILE:
					goteof = true;
					break;
				
					// Parse the package statement
				case PACKAGE:
					this.__parsePackage((__StatePackage__)state);
					break;
					
					// Import statement
				case IMPORT:
					this.__parseImport((__StateImport__)state);
					break;
					
					// Class
				case CLASS:
					this.__parseClass((__StateClass__)state);
					break;
				
					// {@squirreljme.error AQ13 Could not parse the structure
					// because the specified state is not known. (The area)}
				default:
					throw new BasicStructureException(this,
						String.format("AQ13 %s", area));
			}
		}
		
		// Build it
		return this.builder.build();
	}
	
	/**
	 * Parses a class.
	 *
	 * @param __state The parsing state.
	 * @throws BasicStructureException If the structure could not be parsed.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/13
	 */
	private final void __parseClass(__StateClass__ __state)
		throws BasicStructureException, IOException, NullPointerException
	{
		if (__state == null)
			throw new NullPointerException("NARG");
		
		TokenizerLayer layer = this.layer;
		
		LayeredToken next = layer.peek();
		TokenType type = next.type();
		
		// Could this be an annotation or annotation declaration?
		boolean isatinterface = false;
		if (type == TokenType.SYMBOL_AT)
		{
			LayeredToken following = layer.peek(1);
			TokenType followtype = following.type();
			
			// Is an annotation declaration
			if (type != TokenType.KEYWORD_INTERFACE)
				isatinterface = true;
			
			// Is an annotation to be parsed
			else
				throw new todo.TODO();
		}
		
		// 
		
		throw new todo.TODO();
	}
	
	/**
	 * Parses a single import statement.
	 *
	 * @param __state The parsing state.
	 * @throws BasicStructureException If the structure could not be parsed.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/13
	 */
	private final void __parseImport(__StateImport__ __state)
		throws BasicStructureException, IOException, NullPointerException
	{
		if (__state == null)
			throw new NullPointerException("NARG");
		
		TokenizerLayer layer = this.layer;
		
		LayeredToken next = layer.peek();
		TokenType type = next.type();
		
		// An import statement
		if (type == TokenType.KEYWORD_IMPORT)
		{
			// Consume that token
			layer.next();
			
			// Detect static imports
			next = layer.peek();
			type = next.type();
			boolean isstatic = false;
			if (type == TokenType.KEYWORD_STATIC)
			{
				isstatic = true;
				
				// Consume it
				layer.next();
			}
			
			// Go through and parse
			StringBuilder sb = new StringBuilder();
			boolean iswildcard = false;
			for (boolean firstrun = true;; firstrun = false)
			{
				next = layer.next();
				type = next.type();
				
				// Add identifier
				if (type == TokenType.IDENTIFIER)
					sb.append(next.characters());
				
				// Wildcard operation
				else if (type == TokenType.OPERATOR_MULTIPLY)
				{
					// {@squirreljme.error AQ1s Did not expect the wildcard
					// asterisk here. (The token)}
					if (firstrun)
						throw new BasicStructureException(next,
							String.format("AQ1a %s", next));
					
					sb.append('*');
					iswildcard = true;
				}
				
				// {@squirreljme.error AQ19 Expected identifier in import
				// statement. (The token)}
				else
					throw new BasicStructureException(next,
						String.format("AQ19 %s", next));
				
				// Sequence will continue or end
				next = layer.next();
				type = next.type();
				
				// Continues
				if (type == TokenType.SYMBOL_DOT)
				{
					// {@squirreljme.error AQ1b Imports which are wildcard
					// must always have the asterisk followed by a semi-colon.
					// (The next token)}
					if (iswildcard)
						throw new BasicStructureException(next,
							String.format("AQ1b %s", next));
					
					// Is considered a binary name
					sb.append('/');
				}
				
				// Ends here
				else if (type == TokenType.SYMBOL_SEMICOLON)
				{
					this.builder.addImport(new ImportStatement(isstatic,
						new BinaryName(sb.toString()), iswildcard));
					break;
				}
				
				// {@squirreljme.error AQ1c Unexpected token while parsing
				// the import statement. (The next token)}
				else
					throw new BasicStructureException(next,
						String.format("AQ1c %s", next));
			}
		}
		
		// Potential start of class, switch
		else if (type.isPotentialClassStart())
		{
			this.__stateReplace(new __StateClass__(false));
		}
		
		// {@squirreljme.error AQ18 Unxpected token while looking for import
		// statements.}
		else
			throw new BasicStructureException(next,
				String.format("AQ18 %s", next));
	}
	
	/**
	 * Parses the package statement.
	 *
	 * @param __state The parsing state.
	 * @throws BasicStructureException If the structure could not be parsed.
	 * @throws IOException On read errors.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/12
	 */
	private final void __parsePackage(__StatePackage__ __state)
		throws BasicStructureException, IOException, NullPointerException
	{
		if (__state == null)
			throw new NullPointerException("NARG");
		
		TokenizerLayer layer = this.layer;
		
		LayeredToken next = layer.peek();
		TokenType type = next.type();
		
		// Package delaration
		if (type == TokenType.KEYWORD_PACKAGE)
		{
			// Consume package
			layer.next();
			
			// Target string for the package identifier
			StringBuilder sb = new StringBuilder();
			
			// Reading loop, identifier word followed by dot or semicolon
			for (;;)
			{
				// {@squirreljme.error AQ16 Expected identifier while parsing
				// the package. (The token)}
				next = layer.next();
				type = next.type();
				if (type != TokenType.IDENTIFIER)
					throw new BasicStructureException(next,
						String.format("AQ16 %s", next));
				
				// Use this identifier
				sb.append(next.characters());
				
				// Adding another identifier?
				next = layer.next();
				type = next.type();
				if (type == TokenType.SYMBOL_DOT)
				{
					// Use slash because binary name
					sb.append('/');
					continue;
				}
				
				// No more
				else if (type == TokenType.SYMBOL_SEMICOLON)
					break;
				
				// {@squirreljme.error AQ17 Expected either a dot or
				// semi-colon in the package statement. (The token)}
				else
					throw new BasicStructureException(next,
						String.format("AQ17 %s", next));
			}
			
			// Set package
			this.builder.setPackage(new BinaryName(sb.toString()));
			
			// Start reading imports
			this.__stateReplace(new __StateImport__());
		}
		
		// Import statement
		else if (type == TokenType.KEYWORD_IMPORT)
		{
			this.__stateReplace(new __StateImport__());
		}
		
		// Potential start of class, switch
		else if (type.isPotentialClassStart())
		{
			this.__stateReplace(new __StateClass__(false));
		}
		
		// {@squirreljme.error AQ15 Unexpected token while searching for the
		// package keyword or other parts. (The next token)}
		else
			throw new BasicStructureException(next,
				String.format("AQ15 %s", next));
	}


	/**
	 * Pushes a new state to the top.
	 *
	 * @param __new The new state entry.
	 * @return The new state.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/09
	 */
	private final __State__ __statePush(__State__ __new)
		throws NullPointerException
	{
		if (__new == null)
			throw new NullPointerException("NARG");
		
		// Add to the top
		Deque<__State__> states = this._states;
		states.addLast(__new);
		
		return __new;
	}
	
	/**
	 * Replaces the top stack entry with the new type.
	 *
	 * @param __new The new state.
	 * @return The new state.
	 * @throws NullPointerException On null arguments.
	 * @since 2018/03/08
	 */
	private final __State__ __stateReplace(__State__ __new)
		throws NullPointerException
	{
		if (__new == null)
			throw new NullPointerException("NARG");
		
		// Replace the top
		Deque<__State__> states = this._states;
		states.removeLast();
		states.addLast(__new);
		
		return __new;
	}
}

