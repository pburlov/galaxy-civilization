/*
 * $Id: LoginDocumentFilter.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.core.security;

import java.awt.Toolkit;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Filtriert alle Zeichen die nicht Ziffern oder Buchstaben sind und begrenzt die Gesamtlänge des
 * Textes
 * 
 * @author pb
 */
public class LoginDocumentFilter extends DocumentFilter
{

	int maxCharacters;

	public LoginDocumentFilter(int maxChars)
	{
		maxCharacters = maxChars;
	}

	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
	{
		string = checkString(string);
		if((fb.getDocument().getLength() + string.length()) <= maxCharacters)
			super.insertString(fb, offset, string, attr);
		else
			Toolkit.getDefaultToolkit().beep();
	}

	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException
	{
		text = checkString(text);
		if((fb.getDocument().getLength() + text.length() - length) <= maxCharacters)
			super.replace(fb, offset, length, text, attrs);
		else
			Toolkit.getDefaultToolkit().beep();
	}

	private String checkString(String input)
	{
		if(input.length() == 0) return input;
		if(input.length() == 1)
		{
			if(Character.isLetterOrDigit(input.charAt(0))) return input;
			Toolkit.getDefaultToolkit().beep();
			return "";
		}
		/*
		 * Wenn String länger als ein Zeichen ist, dann String scannen
		 */
		boolean beep = false;
		StringBuffer buf = new StringBuffer();
		for(char ch : input.toCharArray())
		{
			if(Character.isLetterOrDigit(ch)) buf.append(ch);
			beep = true;
		}
		if(beep) Toolkit.getDefaultToolkit().beep();
		return buf.toString();
	}
}
