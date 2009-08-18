package com.amalto.workbench.providers;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.ui.IWorkbenchPartSite;
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
import org.w3c.dom.Element;

public class XPathTreeContentProvider extends XSDTreeContentProvider {
	
	public XPathTreeContentProvider(IWorkbenchPartSite site,
			XSDSchema invisibleRoot) {
		super(site, invisibleRoot);
		// TODO Auto-generated constructor stub
	}
	@Override
	public Object[] getChildren(Object parent) {
		if (parent instanceof XSDSchema) {
			EList xsdElementDeclarations = xsdSchema.getElementDeclarations();
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

			//list.addAll(((XSDElementDeclaration)parent).getIdentityConstraintDefinitions());
			
			//the annotations
//			XSDAnnotation annotation = ((XSDElementDeclaration)parent).getAnnotation(); 
//			if (annotation!=null) {
//				list.add(annotation);
//			}
			return list.toArray(new Object[list.size()]);
		}
		
		if (parent instanceof XSDIdentityConstraintDefinition) {			
			ArrayList list = new ArrayList();

//			list.add(((XSDIdentityConstraintDefinition)parent).getSelector());
//			list.addAll(((XSDIdentityConstraintDefinition)parent).getFields());
			
			return list.toArray(new Object[list.size()]);
		}
		
//		if (parent instanceof XSDAnnotation) {
//			XSDAnnotation annotation = (XSDAnnotation) parent;
//			ArrayList list = new ArrayList();
//			list.addAll(annotation.getUserInformation());
//			list.addAll(annotation.getApplicationInformation());
//			return list.size()== 0 ? new Object[0] : list.toArray(new Object[list.size()]);
//		}
		
		if (parent instanceof Element) {	//appinfos
			return new Object[0];
		}
		
		
//		System.out.println("getChildren: UNPROCESSED PARENT: "+parent.getClass().getName()+": "+parent);
		
		return new Object[0];
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

			//list.addAll(((XSDElementDeclaration)term).getIdentityConstraintDefinitions());
			
//			XSDAnnotation annotation = ((XSDElementDeclaration)term).getAnnotation(); 
//			if (annotation!=null) {
//				list.add(annotation);
//			}
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
//		if ("extension".equals(complexTypeDefinition.getDerivationMethod().getName())) {
//			if (complexTypeDefinition.getBaseTypeDefinition() instanceof XSDComplexTypeDefinition) {
//				list.addAll(getComplexTypeDefinitionChildren((XSDComplexTypeDefinition)complexTypeDefinition.getBaseTypeDefinition()));
//			}
//		}
		
		//Attributes
		if (complexTypeDefinition.getAttributeContents()!=null)
			list.addAll(complexTypeDefinition.getAttributeContents());
		
//		//Annotations
//		if (complexTypeDefinition.getAnnotations()!=null)
//			list.addAll(complexTypeDefinition.getAnnotations());
			
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
	
}
