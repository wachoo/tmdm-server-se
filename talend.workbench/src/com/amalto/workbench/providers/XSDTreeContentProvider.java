package com.amalto.workbench.providers;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWildcard;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.workbench.utils.DataModelFilter;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider,ISchemaContentProvider {
	protected XSDSchema xsdSchema;
	IWorkbenchPartSite site = null;
	DataModelFilter filter;
	public XSDTreeContentProvider(IWorkbenchPartSite site, XSDSchema invisibleRoot ) {
		this.site = site;
		this.xsdSchema = invisibleRoot;
	}
	
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}
	
	public void dispose() {
	}
	
	public Object[] getElements(Object parent) {
		if (parent.equals(site)) {
			return getChildren(xsdSchema);
		}
		return getChildren(parent);
	}
	
	public Object getParent(Object child) {
		//System.out.println("get Parent "+child.getClass().getName());
		return null;
	}
	
	
	
	public void setFilter(DataModelFilter filter) {
		this.filter = filter;
	}

	public Object [] getChildren(Object parent) {	
		
		if (parent  == null) {
			System.out.println("NULL PARENT");
		}
//		System.out.println("get Children "+parent.getClass().getName()+ " -- "+parent.toString());

		if (parent instanceof XSDSchema) {
			EList xsdElementDeclarations = xsdSchema.getElementDeclarations();
			List<XSDElementDeclaration> list=new ArrayList<XSDElementDeclaration>();
			if(filter!=null && !filter.isAll()){
				for(XSDElementDeclaration el: (XSDElementDeclaration[])xsdElementDeclarations.toArray(new XSDElementDeclaration[xsdElementDeclarations.size()] )){
					if(isInclude( el.getAnnotation())){
						list.add(el);
					}					
				}
				return list.toArray(new XSDElementDeclaration[list.size()]);
			}
			return xsdElementDeclarations.toArray(new XSDElementDeclaration[xsdElementDeclarations.size()] );
		}
		
		if (parent instanceof XSDAttributeGroupDefinition) {
			XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) parent;
			if (attributeGroupDefinition.getContents().size()==0) //a ref
				attributeGroupDefinition = attributeGroupDefinition.getResolvedAttributeGroupDefinition();
			return attributeGroupDefinition.getContents().toArray(new Object[attributeGroupDefinition.getContents().size()]);
		}
				
		if (parent instanceof XSDParticle) {
			//a particle inside a choice or sequence or whatever
			XSDParticle xsdParticle = (XSDParticle)parent;
			return getXSDParticleChildren(xsdParticle);
		}//XSDParticle
		
		
		
		/*
		if (parent instanceof XSDTypeDefinition) {
			
			if (parent instanceof XSDSimpleTypeDefinition) {
				XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition) parent;
				if (std.getVariety().equals(XSDVariety.ATOMIC_LITERAL)) {
					ArrayList list = new ArrayList();
					//add Base Type if not a pre-defined type
					if (! std.getSchema().getSchemaForSchema().getTypeDefinitions().contains(std))
						list.add(std.getBaseTypeDefinition());
					list.addAll(std.getFacetContents());
					return list.toArray(new Object[list.size()]);
				}
				if (std.getVariety().equals(XSDVariety.LIST_LITERAL)) {
					//FIXME: How do we indicate it is a LIST?
					return new XSDSimpleTypeDefinition[] {std.getBaseTypeDefinition()};
				}
				if (std.getVariety().equals(XSDVariety.UNION_LITERAL))
					return (XSDSimpleTypeDefinition[]) (std.getMemberTypeDefinitions().toArray(new XSDSimpleTypeDefinition[std.getMemberTypeDefinitions().size()]));
				//return new Object[]{std.getBaseTypeDefinition()};
			}
			if (parent instanceof XSDComplexTypeDefinition) {
				XSDComplexTypeContent xsdComplexTypeContent = ((XSDComplexTypeDefinition) parent).getContent();
				
				ArrayList<Object> list = new ArrayList<Object>();
				list.addAll(((XSDComplexTypeDefinition)parent).getAttributeContents());
				
				if  (xsdComplexTypeContent == null) {
					XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition) parent;
					list.add(ctd.getBaseTypeDefinition());
					return list.toArray(new Object[list.size()]);
				} else {
					if (xsdComplexTypeContent instanceof XSDSimpleTypeDefinition) {
						list.add(xsdComplexTypeContent);
						return list.toArray(new Object[list.size()]);
					}
					if (xsdComplexTypeContent instanceof XSDParticle) 
						return getXSDParticleChildren((XSDParticle)xsdComplexTypeContent);
					//return children
					list.add(((XSDComplexTypeDefinition) parent).getContent());
					return list.toArray(new Object[list.size()]);
				}
			}
			
		}
		*/
	
		/*
		if (parent instanceof XSDElementDeclaration) {
			ArrayList<Object> list = new ArrayList<Object>();
					
			//handle extensions and retrictions directly
			XSDTypeDefinition typeDefinition = ((XSDElementDeclaration)parent).getTypeDefinition();
			if (typeDefinition instanceof XSDComplexTypeDefinition) {
				XSDComplexTypeDefinition complexTypeDefinition = (XSDComplexTypeDefinition) typeDefinition;
				if (complexTypeDefinition.getContent() == null)
					list.add(complexTypeDefinition.getBaseTypeDefinition());
				else
					list.add(complexTypeDefinition);
			} else {
				list.add(((XSDElementDeclaration)parent).getTypeDefinition());	
			}

			list.addAll(((XSDElementDeclaration)parent).getIdentityConstraintDefinitions());
			return list.toArray(new Object[list.size()]);
		}
		*/
		
		if (parent instanceof XSDModelGroup) {
			XSDModelGroup modelGroup = ((XSDModelGroup)parent);
			EList list = modelGroup.getContents();
			List<XSDParticle> ls=new ArrayList<XSDParticle>();
			if(filter!=null && !filter.isAll()){
				for(XSDParticle el: (XSDParticle[])list.toArray(new XSDParticle[list.size()] )){
					XSDTerm tm = el.getTerm();
					if (tm instanceof XSDElementDeclaration) {
						XSDAnnotation annotation = ((XSDElementDeclaration)tm).getAnnotation();
						if(isInclude(annotation)){
							ls.add(el);
						}
					}
				}
				return ls.toArray(new XSDParticle[ls.size()]);
			}			
			return list.toArray(new Object[list.size()]);
		}
		
		if (parent instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition std = (XSDSimpleTypeDefinition) parent;
			if (std.getVariety().equals(XSDVariety.ATOMIC_LITERAL)) {
				ArrayList list = new ArrayList();
				//add Base Type if not a pre-defined type
				if (! std.getSchema().getSchemaForSchema().getTypeDefinitions().contains(std))
					list.add(std.getBaseTypeDefinition());
				list.addAll(std.getFacetContents());
				return list.toArray(new Object[list.size()]);
			}
			if (std.getVariety().equals(XSDVariety.LIST_LITERAL)) {
				//FIXME: How do we indicate it is a LIST?
				return new XSDSimpleTypeDefinition[] {std.getBaseTypeDefinition()};
			}
			if (std.getVariety().equals(XSDVariety.UNION_LITERAL))
				return std.getMemberTypeDefinitions().toArray(new XSDSimpleTypeDefinition[std.getMemberTypeDefinitions().size()]);
			//return new Object[]{std.getBaseTypeDefinition()};
		}
		
		if (parent instanceof XSDElementDeclaration) {
			
			//abstract elements do not have children
			if (((XSDElementDeclaration)parent).isAbstract())
				return new Object[0];
			
			ArrayList<Object> list = new ArrayList<Object>();
					
			//handle extensions and restrictions directly
			XSDTypeDefinition typeDefinition = ((XSDElementDeclaration)parent).getTypeDefinition();
			if (typeDefinition instanceof XSDComplexTypeDefinition) {
				list.addAll(getComplexTypeDefinitionChildren((XSDComplexTypeDefinition)typeDefinition));
			} else {
				list.add(((XSDElementDeclaration)parent).getTypeDefinition());	
			}
			//the keys
			if(filter!=null && !filter.isAll()){
				
			}else{
				list.addAll(((XSDElementDeclaration)parent).getIdentityConstraintDefinitions());
			}
			//the annotations
			XSDAnnotation annotation = ((XSDElementDeclaration)parent).getAnnotation(); 
			if(filter!=null && !filter.isAll()){
				
			}else{
				if (annotation!=null) {
					list.add(annotation);
				}
			}
			return list.toArray(new Object[list.size()]);
		}
		
		if (parent instanceof XSDIdentityConstraintDefinition) {
			ArrayList list = new ArrayList();
			if(filter!=null && !filter.isAll()){
				
			}else{
				list.add(((XSDIdentityConstraintDefinition)parent).getSelector());
				list.addAll(((XSDIdentityConstraintDefinition)parent).getFields());
			}
			
			return list.toArray(new Object[list.size()]);
		}
		
		if (parent instanceof XSDAnnotation) {
			ArrayList list = new ArrayList();
			if(filter!=null && !filter.isAll()){
				
			}else{
				XSDAnnotation annotation = (XSDAnnotation) parent;			
				list.addAll(annotation.getUserInformation());
				list.addAll(annotation.getApplicationInformation());
			}
			return list.size()== 0 ? new Object[0] : list.toArray(new Object[list.size()]);
		}
		
		if (parent instanceof Element) {	//appinfos
			return new Object[0];
		}
		
		
//		System.out.println("getChildren: UNPROCESSED PARENT: "+parent.getClass().getName()+": "+parent);
		
		return new Object[0];
	}
	private boolean isInclude(XSDAnnotation annotation){
		boolean include=false;
		if(annotation!=null){
			XSDAnnotationsStructure annotion = new XSDAnnotationsStructure(
					 annotation);
			if((annotion.getWriteAccesses().values().contains(filter.getRole()) && filter.isWriteAccess())){
				return true;
			}
			if((annotion.getHiddenAccesses().values().contains(filter.getRole()) && filter.isHiddenAccess())){
				return true;
			}			
		}else{
			if((filter.isReadOnly())){
				return true;
			}			
		}
		return include;
	}
	private Object[] getXSDParticleChildren(XSDParticle particle) {
//		System.out.println("getXSDParticleChildren() CLASS "+particle.getClass().getName());
//		System.out.println("getXSDParticleChildren() TERM "+particle.getTerm().getClass().getName()+": "+particle.getTerm());
		XSDTerm term = particle.getTerm();
		
		if (term instanceof XSDElementDeclaration) {
			XSDTypeDefinition typeDef = ((XSDElementDeclaration)term).getTypeDefinition();
			if (typeDef == null) return new Object[0]; //elements with not type declaration (allowed)
			ArrayList<Object> list = new ArrayList<Object>();
			
			if (typeDef instanceof XSDComplexTypeDefinition) {
				list.addAll(getComplexTypeDefinitionChildren((XSDComplexTypeDefinition)typeDef));
			} else {
				list.add(typeDef);
			}
			list.addAll(((XSDElementDeclaration)term).getIdentityConstraintDefinitions());
			XSDAnnotation annotation = ((XSDElementDeclaration)term).getAnnotation(); 
			if(filter!=null && !filter.isAll()){
				
			}else{
				if (annotation!=null) {
					list.add(annotation);
				}
			}
			return list.toArray(new Object[list.size()]);
		}		
		
		if (term instanceof XSDModelGroup) {//a ModelGroup skip it and get children directtly
			EList list = ((XSDModelGroup) term).getContents();
			return list.toArray(new XSDParticle[list.size()]); 
		}
		
		if (term instanceof XSDWildcard)  {}
			
		return new Object[]{};

	}
	
	private ArrayList<Object> getComplexTypeDefinitionChildren(XSDComplexTypeDefinition complexTypeDefinition) {
//		System.out.println("getComplexTypeDefinitionChildren "+complexTypeDefinition+": "+complexTypeDefinition.getContent());
		
//		System.out.println(
//				"getComplexTypeDefinitionChildren BASE TYPE "+
//				complexTypeDefinition.getBaseTypeDefinition().getName()+" : "+
//				complexTypeDefinition.getDerivationMethod().getName()
//		);
		
		XSDComplexTypeContent xsdComplexTypeContent = complexTypeDefinition.getContent();
		ArrayList<Object> list = new ArrayList<Object>();
		
		//Now determine whether ref. If ref look at the base Type definition
		if  (xsdComplexTypeContent == null) {
			XSDTypeDefinition typeDefinition = complexTypeDefinition.getBaseTypeDefinition();
			//if a simple type return the simple type
			if (typeDefinition instanceof XSDSimpleTypeDefinition) {
				list.add(typeDefinition);
				return list;
			} else {
			}
			//it is a complex Type
			xsdComplexTypeContent = ((XSDComplexTypeDefinition)typeDefinition).getContent();
		}

		//check if we are extending a complex Definition	
		if ("extension".equals(complexTypeDefinition.getDerivationMethod().getName())) {
			if (complexTypeDefinition.getBaseTypeDefinition() instanceof XSDComplexTypeDefinition) {
				list.addAll(getComplexTypeDefinitionChildren((XSDComplexTypeDefinition)complexTypeDefinition.getBaseTypeDefinition()));
			}
		}
		
		//Attributes
		if (complexTypeDefinition.getAttributeContents()!=null)
			list.addAll(complexTypeDefinition.getAttributeContents());
		
		//Annotations
		if (complexTypeDefinition.getAnnotations()!=null)
			list.addAll(complexTypeDefinition.getAnnotations());
			
		//now check what we have in the content
			
		//simple type return the simple type
		if (xsdComplexTypeContent instanceof XSDSimpleTypeDefinition) {
			list.add(xsdComplexTypeContent);
			return list;
		}
		
		//xsd Particle: we have a model group
		if (xsdComplexTypeContent instanceof XSDParticle) { 
//				System.out.println("Model Group?: "+((XSDParticle)xsdComplexTypeContent).getTerm());
				if (((XSDParticle)xsdComplexTypeContent).getTerm() instanceof XSDModelGroup) {
					//return the model group
					list.add(((XSDParticle)xsdComplexTypeContent).getTerm());
					return list;
				} else {  //wild card or element declaration '?)
					list.add(((XSDParticle)xsdComplexTypeContent).getTerm());
					return list;						
				}
		}
		
		//what else could it be ?
		list.add(xsdComplexTypeContent);
		return list;
	}
	
	public boolean hasChildren(Object parent) {
		//System.out.println("has Children "+parent.getClass().getName());
		return getChildren(parent).length >0;
	}

	public XSDSchema getXsdSchema() {
		return xsdSchema;
	}
	
	public void setXsdSchema(String xsd)
	{
		xsdSchema = Util.createXsdSchema(xsd);
	}
	public void setXsdSchema(XSDSchema xsdSchema){
		this.xsdSchema=xsdSchema;
	}
	
	public String getXSDSchemaAsString() throws Exception{
		Document document =  xsdSchema.getDocument();
		String schema = Util.nodeToString(document);

		//FIXES a bug in the XSD library that puts nillable attributes in elements which are ref
		//That is illegal according to W3C §3.3.3 / 2.2 (and Xerces) 
		Pattern p  = Pattern.compile("(<([a-z]+:)?element.*?)nillable=['|\"].*?['|\"](.*?ref=.*?>)");
		schema = p.matcher(schema).replaceAll("$1 $3");

		return schema;
	}

	/*
	public void addListener(IXObjectModelListener listener) {
		xsdSchema.
	}
	public void removeListener(IXObjectModelListener listener) {
		invisibleRoot.removeListener(listener);
	}
	*/
	

}
