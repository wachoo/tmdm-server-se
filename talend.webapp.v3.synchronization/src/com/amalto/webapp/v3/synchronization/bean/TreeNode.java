package com.amalto.webapp.v3.synchronization.bean;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;


public class TreeNode {
	private String text;
	private boolean leaf;
	private short type;//Element.TEXT_NODE or Element.ELEMENT_NODE
	private TreeNode[] childNodes = new TreeNode[0];
	

	public short getType() {
		return type;
	}
	public void setType(short type) {
		this.type = type;
	}
	public TreeNode[] getChildNodes() {
		return childNodes;
	}
	public void setChildNodes(TreeNode[] childNodes) {
		this.childNodes = childNodes;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
	public String serialize(){
		return getXMLStr(this);
	}
	private String getXMLStr(TreeNode node){
		StringBuffer sb =new StringBuffer();
		if(node.text.trim().length()>0){
			sb.append("<"+node.text+">");
			for(TreeNode n: node.childNodes){
				if(n.isLeaf() && n.text.trim().length()>0){
					if(n.type == Node.TEXT_NODE)
						sb.append(n.text);
					else if(n.type == Node.ELEMENT_NODE)
						sb.append("<"+n.text + "/>");
				}else{
					sb.append(getXMLStr(n));
				}
			}
			sb.append("</"+node.text+">");
		}
		return sb.toString();
	}
	
	public TreeNode deserialize(String xml)throws Exception{
		if(xml==null) return null;
		Element result = Util.parse(xml).getDocumentElement();		
		return parseElement(result);		
	}
	
	private TreeNode parseElement(Element element)throws Exception{
		TreeNode node=new TreeNode();		
		node.setType(Element.ELEMENT_NODE);
		node.setText( element.getNodeName());
		NodeList list=element.getChildNodes();
		List<TreeNode> childs=new ArrayList<TreeNode>();		
		if (list!=null && list.getLength()>0) {
			for (int i = 0; i < list.getLength(); i++) {
				Node el = (Node) list.item(i);
				if(el.getNodeType()==Element.TEXT_NODE && el.getNodeValue().trim().length()>0){					
					TreeNode child=new TreeNode();
					child.setText(el.getNodeValue().trim());
					child.setLeaf(true);
					child.setType(Element.TEXT_NODE);
					childs.add(child);
				}else if(el.getNodeType()==Element.ELEMENT_NODE){
					
					childs.add(parseElement((Element)el));
				}
			}
			node.setLeaf(false);
		}else{
			node.setLeaf(true);
		}
		node.setChildNodes(childs.toArray(new TreeNode[0]));
		return node;
	}

}
