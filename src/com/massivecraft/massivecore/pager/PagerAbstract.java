package com.massivecraft.massivecore.pager;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.massivecore.cmd.MassiveCommand;
import com.massivecraft.massivecore.mson.Mson;
import com.massivecraft.massivecore.util.Txt;

public abstract class PagerAbstract<T> implements Pager<T>
{
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //
	
	public int size()
	{
		return (int) Math.ceil((double) this.getItems().size() / this.getItemsPerPage());
	}
	
	public boolean isValid(int number)
	{
		if (this.isEmpty()) return false;
		if (number < 1) return false;
		if (number > this.size()) return false;
		return true;
	}
	
	public boolean isEmpty()
	{
		return this.getItems().isEmpty();
	}
	
	@SuppressWarnings("unchecked")
	public List<T> get(int number)
	{
		// Return null if the page number is invalid
		if (!this.isValid(number)) return null;
		
		// Forge list from collection
		List<T> items = null;
		if (this.getItems() instanceof List)
		{
			items = (List<T>) this.getItems();
		}
		else
		{
			items = new ArrayList<T>(this.getItems());
		}
		
		int index = number - 1;
		
		// Calculate from and to
		int from = index * this.getItemsPerPage();
		int to = from + this.getItemsPerPage();
		if (to > items.size())
		{
			to = items.size();
		}
		
		// Pick them
		return items.subList(from, to);
	}
	
	// -------------------------------------------- //
	// TXT
	// -------------------------------------------- //
	
	public String getMessageEmpty()
	{
		return Txt.getMessageEmpty().toPlain(true);
	}
	
	public String getMessageInvalid()
	{
		return Txt.getMessageInvalid(this.size()).toPlain(true);
	}
	
	@Override
	public List<String> getPageTxt(int number, String title, Stringifier<T> stringifier)
	{
		List<String> ret = new ArrayList<String>();
		
		List<Mson> msons = getPageMson(number, title, new Msonifier<T>(){

			@Override
			public Mson toMson(T item, int index)
			{
				return Mson.mson(stringifier.toString(item, index));
			}
			
		}, null, null);
		
		for (Mson mson : msons)
		{
			ret.add(mson.toPlain(true));
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// Mson
	// -------------------------------------------- //
	
	public List<Mson> getPageMson(int number, String title, Msonifier<T> msonifier, MassiveCommand command, List<String> args)
	{	
		List<Mson> ret = new ArrayList<Mson>();
		
		ret.add(Txt.titleizeMson(title, this.size(), number, command, args));
		
		if (this.isEmpty())
		{
			ret.add(Txt.getMessageEmpty());
			return ret;
		}
		
		List<T> pageItems = this.get(number);
		
		if (pageItems == null)
		{
			ret.add(Txt.getMessageInvalid(this.size()));
			return ret;
		}
		
		int index = (number - 1) * this.getItemsPerPage();
		for (T pageItem : pageItems)
		{
			if (msonifier != null)
			{
				ret.add(msonifier.toMson(pageItem, index));
			}
			else
			{
				ret.add(Mson.mson(pageItem.toString()));
			}
			index++;
		}
		
		return ret;
	}
	
}
