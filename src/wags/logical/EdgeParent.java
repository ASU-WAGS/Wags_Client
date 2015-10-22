package wags.logical;


import org.vaadin.gwtgraphics.client.Line;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.IsSerializable;

public abstract class EdgeParent implements IsSerializable
{
	protected EdgeCollection ec;
	protected ClickHandler handler;
	protected Line line;
	protected Node n1;
	protected Node n2;
	protected boolean removable;
	protected int weight;
	
	public EdgeParent(Node n1, Node n2, EdgeCollection ec, ClickHandler handler, boolean removable)
	{
		this.n1 = n1;
		this.n2 = n2;
		this.ec = ec;
		this.handler = handler;
		this.removable = removable;
		this.weight=0;
	}
	public EdgeParent(Node n1, Node n2, EdgeCollection ec, ClickHandler handler, boolean removable, int weight)
	{
		this.n1 = n1;
		this.n2 = n2;
		this.ec = ec;
		this.handler = handler;
		this.removable = removable;
		this.weight=weight;
	}
	
	@Override
	public boolean equals(Object compare) {
		try {
			EdgeParent ep = (EdgeParent) compare;
			if ((getN1().equals(ep.getN1()) && getN2().equals(ep.getN2())) 
					|| (getN2().equals(ep.getN1()) && getN1().equals(ep.getN2()))) {
					return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public Node getN1()
	{
		return n1;
	}
	
	public Node getN2()
	{
		return n2;
	}
	
	public void setLine(Line line) {
		this.line = line;
	}
	
	public void setN1(Node node)
	{
		this.n1 = node;
	}
	
	public void setN2(Node node)
	{
		this.n2 = node;
	}
	
	public Line getLine()
	{
		return line;
	}
	
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public boolean isWeightedEdge(){
		return weight!=0;
	}
	
	public String getWeightedEdge() {
		return weight + "";
	}
	
	@Override
	public String toString() {
		return "Weight: "+this.weight+" N1: "+n1.toString()+" N2: "+n2.toString();
	}
	
	public abstract void drawEdge();
	
	
}
