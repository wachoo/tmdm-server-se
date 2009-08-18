package com.amalto.workbench.editors.xmleditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class XMLDocumentProvider extends FileDocumentProvider {
//	XMLEditor editor;
//	public XMLDocumentProvider(XMLEditor editor){
//		this.editor=editor;
//	}
	protected IDocument createDocument(Object element) throws CoreException {
		if(element instanceof XMLEditorInput){
			//IDocument document = super.createDocument(element);
			IDocument document=((XMLEditorInput)element).getDocument();
			if (document != null) {
				IDocumentPartitioner partitioner =
					new FastPartitioner(
						new XMLPartitionScanner(),
						new String[] {
							XMLPartitionScanner.XML_TAG,
							XMLPartitionScanner.XML_COMMENT });
				partitioner.connect(document);
				document.setDocumentPartitioner(partitioner);
			}
			return document;
		}
		return null;
	}
	
	public boolean isReadOnly(Object element) {
		return false;
	}
}