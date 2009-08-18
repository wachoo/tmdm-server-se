package com.amalto.workbench.providers;

import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDAttributeUseCategory;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDVariety;
import org.eclipse.xsd.XSDWildcard;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class TypesLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		
//		System.out.println("getText   "+obj.getClass().getName());
		
		if (obj instanceof XSDElementDeclaration) {
			String name = ((XSDElementDeclaration)obj).getName();
			if (((XSDElementDeclaration)obj).isAbstract())
				name+= "   (abstract)";
			return name;
		}
		
		if (obj instanceof XSDParticle) {
			XSDParticle xsdParticle = (XSDParticle)obj;
			XSDParticleContent content = xsdParticle.getContent();
			XSDTerm xsdTerm = xsdParticle.getTerm();
			String name = "";
			if (content instanceof XSDElementDeclaration) {
				XSDElementDeclaration decl = (XSDElementDeclaration) content;
				name+= (decl.getName()==null ? "" : decl.getName());
				if (decl.getTypeDefinition() == null) {
					name+=" ["+((XSDElementDeclaration)xsdTerm).getName()+"]";
				}
			} else if (content instanceof XSDModelGroup) {
				//System.out.println("SHOULD NOT HAPPEN????");
				if (xsdParticle.getContainer() instanceof XSDComplexTypeDefinition) {
					String ctdName = ((XSDComplexTypeDefinition)xsdParticle.getContainer()).getName();
					name = (ctdName != null ? ctdName : "");
				}
				/*
				int type = ((XSDModelGroup)xsdTerm).getCompositor().getValue();
				switch (type) {
					case XSDCompositor.ALL:
						name= "";
						break;
					case XSDCompositor.CHOICE:
						name= "";
						break;
					case XSDCompositor.SEQUENCE:
						name= "";
						break;				
				}
				*/
			} else {
				name = "[Any]";
			}
			if (! ((xsdParticle.getMinOccurs() == 1) && (xsdParticle.getMaxOccurs() == 1))) {			
				name+="  [";
				name+=xsdParticle.getMinOccurs();
				name+="...";
				name+= (xsdParticle.getMaxOccurs() == -1) ? "many" : ""+xsdParticle.getMaxOccurs();
				name+="]";
			}
			return name;
		} 
		
		if (obj instanceof XSDSimpleTypeDefinition) {
			return getSimpleTypeDefinition((XSDSimpleTypeDefinition)obj);
		}
		if(obj instanceof XSDComplexTypeDefinition){
			return getComplexTypeDefinition((XSDComplexTypeDefinition)obj);
		}
		
		if (obj instanceof XSDModelGroup) {
			//return the name of the complex type definition
			XSDParticle particle = (XSDParticle)(((XSDModelGroup)obj).getContainer());
			String name = ((XSDComplexTypeDefinition)particle.getContainer()).getName();
			if (name==null) name = "";
			//return the occurence
			if (! ((particle.getMinOccurs() == 1) && (particle.getMaxOccurs() == 1))) {			
				name+="  [";
				name+=particle.getMinOccurs();
				name+="...";
				name+= (particle.getMaxOccurs() == -1) ? "many" : ""+particle.getMaxOccurs();
				name+="]";
			}
			return name;

		}
		
		/*
		if (obj instanceof XSDComplexTypeDefinition) {
			
			XSDComplexTypeDefinition ctd =  (XSDComplexTypeDefinition)obj;
			
			if (ctd.getContent()==null) {
				if (ctd.getDerivationMethod().equals(XSDDerivationMethod.EXTENSION_LITERAL)) {
					return "ext ["+ctd.getBaseTypeDefinition().getName()+"]["+ctd.getBaseTypeDefinition()+"]";
				} else {
					return "res ["+ctd.getBaseTypeDefinition().getName()+"]["+ctd.getBaseTypeDefinition()+"]";
				}
			} else {			
				String name = ((XSDComplexTypeDefinition)obj).getName(); 
				return (name == null ? "" : name);
			}
		}
		*/
		
		if (obj instanceof XSDFacet) {
			return 	((XSDFacet)obj).getFacetName()+
							": "+((XSDFacet)obj).getLexicalValue();
		}

		if (obj instanceof XSDIdentityConstraintDefinition) {
			return ((XSDIdentityConstraintDefinition)obj).getName();
		}

		if (obj instanceof XSDXPathDefinition) {
			XSDXPathDefinition xpath = (XSDXPathDefinition) obj;
			return xpath.getValue();
		}
		
		if (obj instanceof XSDAttributeGroupDefinition) {
			XSDAttributeGroupDefinition attributeGroupDefinition = (XSDAttributeGroupDefinition) obj;
			String name = (attributeGroupDefinition.getName() == null ? "" : attributeGroupDefinition.getName()); 
			if (attributeGroupDefinition.getContents().size()==0) //a ref
				name+= " ["+attributeGroupDefinition.getResolvedAttributeGroupDefinition().getName()+"]";
			return name;
		}
		
		if (obj instanceof XSDAttributeUse) {
			XSDAttributeUse attributeUse = (XSDAttributeUse) obj;
			String name = attributeUse.getAttributeDeclaration().getName();
			if (name == null) 
				name = " ["+attributeUse.getAttributeDeclaration().getResolvedAttributeDeclaration().getName()+"]";
			return name;
		}
		
		if (obj instanceof XSDAnnotation) {
			//XSDAnnotation annotation = (XSDAnnotation) obj;
			return "Annotations";
		}
		
		if (obj instanceof Element) {
			try {
				Element e = (Element) obj;
				if (e.getLocalName().equals("documentation")) {
					return "Documentation: "+e.getChildNodes().item(0).getNodeValue();
				} else if (e.getLocalName().equals("appinfo")) {
					String source = e.getAttribute("source");
					if (source!=null) {
						if (source.startsWith("X_Label_")) {
							return Util.iso2lang.get(source.substring(8).toLowerCase())+" Label: "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_ForeignKey")) {
							return "Foreign Key:  "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_ForeignKeyInfo")) {
							return "Foreign Key Info:  "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_SourceSystem")) {
							return "Source System:  "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_TargetSystem")) {
							return "Target System(s):  "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.startsWith("X_Description_")) {
							return Util.iso2lang.get(source.substring(14).toLowerCase())+" Description: "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_Write")) {
							return "Writable By : "+e.getChildNodes().item(0).getNodeValue();
						} else if (source.equals("X_Hide")) {
							return "Hidden to : "+e.getChildNodes().item(0).getNodeValue();
						} else {
							return source+": "+Util.nodeToString((Element)obj);
						}
					} else {
						return Util.nodeToString((Element)obj);
					}
				} else {
					return Util.nodeToString((Element)obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		if (obj==null) return "NULL";
		return "?? "+obj.getClass().getName()+" : "+obj.toString();

	}
	
	
	
	
	
	
	
	
	
	
	public Image getImage(Object obj) {
		
//		System.out.println("getImage "+obj.getClass().getName());
		
		if (obj instanceof XSDElementDeclaration) {
			//top declaration
			XSDElementDeclaration decl = (XSDElementDeclaration)obj;
			//check if concept or "just" element
			boolean isConcept=false;
			EList l = decl.getIdentityConstraintDefinitions();
			for (Iterator iter = l.iterator(); iter.hasNext(); ) {
				XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
				if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
					isConcept=true;
					break;
				}
			}
			//display approprite image
			if (isConcept) {
				return ImageCache.getCreatedImage( "icons/concept.gif");
			} else {
				return ImageCache.getCreatedImage( "icons/element_only.gif");
				/*
				if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition)
					return PlatformUI.getWorkbench().getSharedImages().getCreatedImage(ISharedImages.IMG_OBJ_FOLDER);
				else
					return ImageCache.getCreatedImage( "icons/elements_obj_+.gif");
				*/
			}
		}
		
		if (obj instanceof XSDParticle) {
			XSDParticle xsdParticle = (XSDParticle)obj;
			XSDTerm xsdTerm = xsdParticle.getTerm();
			if (xsdTerm instanceof XSDElementDeclaration) {
				//get Type of Parent Group
				if (Util.getKeyInfo((XSDElementDeclaration)xsdTerm) != null)
					return ImageCache.getCreatedImage( "icons/key.gif");
				XSDConcreteComponent xsdConcreteComponent =  xsdParticle.getContainer();
				if (xsdConcreteComponent instanceof XSDModelGroup) {
					if (((XSDModelGroup)xsdConcreteComponent).getCompositor() == XSDCompositor.CHOICE_LITERAL)
						return ImageCache.getCreatedImage( "icons/elements_obj_choice.gif");
					return ImageCache.getCreatedImage( "icons/elements_obj_sequence.gif");
				}
			} else if (xsdTerm instanceof XSDModelGroup) {
				int type = ((XSDModelGroup)xsdTerm).getCompositor().getValue();
				switch (type) {
					case XSDCompositor.ALL:
						return ImageCache.getCreatedImage( "icons/complex_all.gif");
					case XSDCompositor.CHOICE:
						return ImageCache.getCreatedImage( "icons/complex_choice.gif");
					case XSDCompositor.SEQUENCE:
						return ImageCache.getCreatedImage( "icons/complex_sequence.gif");				
				}
			} else if (xsdTerm instanceof XSDWildcard) {
				return ImageCache.getCreatedImage( "icons/wildcard.gif");
			} else {
				System.out.println("ERROR XSD Term "+xsdTerm.getClass().getName());
				return ImageCache.getCreatedImage( "icons/error.gif");
			}			
		}
		
		if (obj instanceof XSDSimpleTypeDefinition) {
			return ImageCache.getCreatedImage( "icons/type_definition.gif");
		}
		
		/*
		if (obj instanceof XSDComplexTypeDefinition) {
			XSDComplexTypeDefinition ctd = (XSDComplexTypeDefinition) obj;
			XSDComplexTypeContent ctc = ctd.getContent();
			if (ctc instanceof XSDParticle) {
				if (((XSDParticle)ctc).getTerm() instanceof XSDModelGroup) {
					int type = ((XSDModelGroup)((XSDParticle)ctc).getTerm()).getCompositor().getValue();
					switch (type) {
						case XSDCompositor.ALL:
							return ImageCache.getCreatedImage( "icons/complex_all.gif");
						case XSDCompositor.CHOICE:
							return ImageCache.getCreatedImage( "icons/complex_choice.gif");
						case XSDCompositor.SEQUENCE:
							return ImageCache.getCreatedImage( "icons/complex_sequence.gif");				
					}
				}
			} else {
				//simple Type!!!
				return ImageCache.getCreatedImage( "icons/error.gif");
			}
		}
		*/
		if (obj instanceof XSDModelGroup) {
			int type = ((XSDModelGroup)obj).getCompositor().getValue();
			switch (type) {
				case XSDCompositor.ALL:
					return ImageCache.getCreatedImage( "icons/complex_all.gif");
				case XSDCompositor.CHOICE:
					return ImageCache.getCreatedImage( "icons/complex_choice.gif");
				case XSDCompositor.SEQUENCE:
					return ImageCache.getCreatedImage( "icons/complex_sequence.gif");
			}
		}

		
		
		if (obj instanceof XSDFacet) {
			return ImageCache.getCreatedImage( "icons/facet.gif");
		}
		
		if (obj instanceof XSDIdentityConstraintDefinition) {
			XSDIdentityConstraintDefinition identity = (XSDIdentityConstraintDefinition) obj;
			if (identity.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
				return ImageCache.getCreatedImage( "icons/unique.gif");
			return ImageCache.getCreatedImage( "icons/key.gif");
		}

		if (obj instanceof XSDXPathDefinition) {
			XSDXPathDefinition xpath = (XSDXPathDefinition) obj;
			if (xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
				return ImageCache.getCreatedImage( "icons/field.gif");
			return ImageCache.getCreatedImage( "icons/selector.gif");
		}
		
		if (obj instanceof XSDAttributeGroupDefinition) {
			return ImageCache.getCreatedImage( "icons/attribute_group.gif");
		}

		if (obj instanceof XSDAttributeUse) {
			XSDAttributeUse att = (XSDAttributeUse) obj;
			if ("xmlns".equals(att.getAttributeDeclaration().getTargetNamespace())) {
				return ImageCache.getCreatedImage( "icons/annotation.gif");
			}
			if (att.getUse().equals(XSDAttributeUseCategory.REQUIRED_LITERAL))
				return ImageCache.getCreatedImage( "icons/attribute_mandatory.gif");
			else
				return ImageCache.getCreatedImage( "icons/attribute.gif");
		}

		if (obj instanceof XSDAnnotation) {
			return ImageCache.getCreatedImage( "icons/annotation.gif");
		}

		if (obj instanceof Element) {
			try {
				Element e = (Element) obj;
				if (e.getLocalName().equals("documentation")) { 
					return ImageCache.getCreatedImage( "icons/documentation.gif");
				} else  if (e.getLocalName().equals("appinfo")) {
					String source = e.getAttribute("source");
					if (source!=null) {
						if (source.startsWith("X_Label_")) {
							return ImageCache.getCreatedImage( "icons/label.gif");
						} else if (source.equals("X_ForeignKey")) {
							return ImageCache.getCreatedImage( "icons/key.gif");
						} else if (source.equals("X_ForeignKeyInfo")) {
							return ImageCache.getCreatedImage( "icons/info.gif");
						} else if (source.equals("X_SourceSystem")) {
							return ImageCache.getCreatedImage( "icons/sourcesystem.gif");
						} else if (source.equals("X_TargetSystem")) {
							return ImageCache.getCreatedImage( "icons/targetsystem.gif");
						} else if (source.startsWith("X_Description_")) {
							return ImageCache.getCreatedImage( "icons/documentation.gif");
						} else if (source.equals("X_Write")) {
							return ImageCache.getCreatedImage( "icons/writable.gif");
						} else if (source.equals("X_Hide")) {
							return ImageCache.getCreatedImage( "icons/hideable.gif");
						} else {
							return ImageCache.getCreatedImage( "icons/documentation.gif");
						}
					} else {
						return ImageCache.getCreatedImage( "icons/documentation.gif");
					}
				} else {
					return ImageCache.getCreatedImage( "icons/documentation.gif");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		
		return ImageCache.getCreatedImage( "icons/tree_object.gif");
		//return PlatformUI.getWorkbench().getSharedImages().getCreatedImage(ISharedImages.IMG_OBJ_ELEMENT);		
	}
	
	public String getComplexTypeDefinition(XSDComplexTypeDefinition definition){
		String s="";
		s=definition.getName();
		
		return s;
	}
	
	/**
	 * Print a simple type definition for the document.
	 * 
	 * @param xsdSimpleTypeDefinition
	 *            a simple type definition in the schema for schema.
	 */
	public String getSimpleTypeDefinition(XSDSimpleTypeDefinition xsdSimpleTypeDefinition) {
		String s = "";
		if (xsdSimpleTypeDefinition == null) {
		} else if (xsdSimpleTypeDefinition.getEffectiveEnumerationFacet() != null) {
			List value = xsdSimpleTypeDefinition.getEffectiveEnumerationFacet().getValue();
			if (value.size() > 1) {
				s+= "(";
			}
			for (Iterator enumerators = value.iterator(); enumerators.hasNext();) {
				String enumerator = enumerators.next().toString();
				s+= enumerator;
				if (enumerators.hasNext()) {
					s+= " | ;";
				}
			}
			if (value.size() > 1) {
				s+= ")";
			}
		} else if (xsdSimpleTypeDefinition.getElement() != null && xsdSimpleTypeDefinition.getElement().hasAttribute(XSDConstants.ID_ATTRIBUTE)) {
			s+= xsdSimpleTypeDefinition.getName();
		} else if ((XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety()) | (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())) {
			s+= "(";
			for (Iterator members = xsdSimpleTypeDefinition.getMemberTypeDefinitions().iterator(); members.hasNext();) {
				XSDSimpleTypeDefinition memberTypeDefinition = (XSDSimpleTypeDefinition) members.next();
				s+=getSimpleTypeDefinition(memberTypeDefinition);
				if (members.hasNext()) {
					s+= " | ";
				}
			}
			s+= ")";
		} else if ((XSDVariety.UNION_LITERAL == xsdSimpleTypeDefinition.getVariety()) | (XSDVariety.LIST_LITERAL == xsdSimpleTypeDefinition.getVariety())) {
			s+= "List of ";
			s+= getSimpleTypeDefinition(xsdSimpleTypeDefinition.getItemTypeDefinition());
		} else if (xsdSimpleTypeDefinition.getName() != null) {
				s+= xsdSimpleTypeDefinition.getName();
		} else if (xsdSimpleTypeDefinition.getEffectivePatternFacet() != null) {
			// s+= xsdSimpleTypeDefinition.getEffectivePatternFacet().getLexicalValue());
			s+= "a restricted xpath expression";
		} else {
			s+= "***";
		}
		return s;
	}
}
