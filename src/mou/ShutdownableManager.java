/*
 * $Id$ Created on Mar 25, 2006 Copyright Paul Burlov 2001-2006
 */
package mou;

import java.util.Stack;

/**
 * Klasse zum geordneten Shutdown von registrierten Shutdownable-Objecten
 */
public class ShutdownableManager
		implements Shutdownable
{

	private Stack shutdownable_objects = new Stack();
	private boolean shutdown = false;

	public ShutdownableManager()
	{
	}

	/**
	 * Registriert den Shutdownable-Object in der Liste Die registrierten Objecte werden in der
	 * umgekehrte Registreirungsreihenfolge heruntergefahren
	 */
	public void addShutdownable(Shutdownable target)
	{
		if(shutdown) throw new IllegalStateException("ShutdownableManager ist bereits heruntergefahren");
		if(!shutdownable_objects.contains(target)) shutdownable_objects.push(target);
	}

	public void removeShutdownable(Shutdownable target)
	{
		if(shutdownable_objects.contains(target)) shutdownable_objects.remove(target);
	}

	/**
	 * Fährt alle registrierten Shutdownable-Objecte herunter.
	 */
	public void shutdown()
	{
		if(shutdown) return;
		while(!shutdownable_objects.empty())
		{
			Shutdownable object = (Shutdownable) shutdownable_objects.pop();
			object.shutdown();
		}
		shutdown = true;
	}
}