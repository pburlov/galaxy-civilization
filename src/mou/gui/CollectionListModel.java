/*
 * $Id: CollectionListModel.java 12 2006-03-25 16:19:37Z root $ Created on Mar 25, 2006 Copyright
 * Paul Burlov 2001-2006
 */
package mou.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * List Model die zum Collection-Framework kompatibel implementiert wurde. Wird anstelle von
 * DefaultListModel verwendet
 * 
 * @author pbu
 */
public class CollectionListModel extends AbstractListModel
{

	private List mData;

	public CollectionListModel()
	{
		mData = new ArrayList();
	}

	/**
	 * 
	 */
	public CollectionListModel(List data)
	{
		setListData(data);
	}

	public void setListData(List data)
	{
		mData = data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize()
	{
		return mData.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index)
	{
		return mData.get(index);
	}

	public List getListData()
	{
		return mData;
	}

	public void addElement(Object o)
	{
		mData.add(mData.size(), o);
		fireIntervalAdded(this, mData.size() - 1, mData.size() - 1);
	}

	public void removeElement(int index)
	{
		if(index < 0 || index >= mData.size()) return;
		mData.remove(index);
		fireIntervalRemoved(this, index, mData.size() > 0 ? mData.size() - 1 : index);
	}

	public void sort()
	{
		if(mData.size() < 2) return;
		Collections.sort(mData);
		fireContentsChanged(this, 0, mData.size() - 1);
	}

	public void sort(Comparator comparator)
	{
		if(mData.size() < 2) return;
		Collections.sort(mData, comparator);
		fireContentsChanged(this, 0, mData.size() - 1);
	}
}
