/********************************************************************************
 * This file is part of the api for NCL authoring - aNa.
 *
 * Copyright (c) 2011, MídiaCom Lab (www.midiacom.uff.br)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement:
 *        This product includes the Api for NCL Authoring - aNa
 *        (http://joeldossantos.github.com/aNa).
 *
 *  * Neither the name of the lab nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without specific
 *    prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY MÍDIACOM LAB AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE MÍDIACOM LAB OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *******************************************************************************/
package br.uff.midiacom.ana.connector;

import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.NCLElementImpl;
import br.uff.midiacom.ana.NCLIdentifiableElement;
import br.uff.midiacom.ana.NCLModificationListener;
import br.uff.midiacom.ana.NCLParsingException;
import br.uff.midiacom.ana.datatype.enums.NCLElementAttributes;
import br.uff.midiacom.ana.datatype.enums.NCLElementSets;
import br.uff.midiacom.ana.datatype.enums.NCLImportType;
import br.uff.midiacom.ana.datatype.ncl.connector.NCLConnectorBasePrototype;
import br.uff.midiacom.ana.reuse.NCLImport;
import br.uff.midiacom.xml.XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class NCLConnectorBase<T extends NCLConnectorBase, P extends NCLElement, I extends NCLElementImpl, Ec extends NCLCausalConnector, Ei extends NCLImport>
        extends NCLConnectorBasePrototype<T, P, I, Ec, Ei> implements NCLIdentifiableElement<T, P> {


    public NCLConnectorBase() throws XMLException {
        super();
    }


    @Override
    protected void createImpl() throws XMLException {
        impl = (I) new NCLElementImpl<T, P>(this);
    }


    @Override
    public boolean addCausalConnector(Ec connector) throws XMLException {
        if(super.addCausalConnector(connector)){
            impl.notifyInserted(NCLElementSets.CONNECTORS, connector);
            return true;
        }
        return false;
    }
    
    
    @Override
    public boolean removeCausalConnector(Ec connector) throws XMLException {
        if(super.removeCausalConnector(connector)){
            impl.notifyRemoved(NCLElementSets.CONNECTORS, connector);
            return true;
        }
        return false;
    }


    @Override
    public boolean removeCausalConnector(String id) throws XMLException {
        if(super.removeCausalConnector(id)){
            impl.notifyRemoved(NCLElementSets.CONNECTORS, id);
            return true;
        }
        return false;
    }
    
    
    @Override
    public boolean addImportBase(Ei importBase) throws XMLException {
        if(super.addImportBase(importBase)){
            impl.notifyInserted(NCLElementSets.IMPORTS, importBase);
            return true;
        }
        return false;
    }


    @Override
    public boolean removeImportBase(Ei importBase) throws XMLException {
        if(super.removeImportBase(importBase)){
            impl.notifyRemoved(NCLElementSets.IMPORTS, importBase);
            return true;
        }
        return false;
    }


    public void load(Element element) throws NCLParsingException {
        String att_name, att_var;
        NodeList nl;

        try{
            // set the id (optional)
            att_name = NCLElementAttributes.ID.toString();
            if(!(att_var = element.getAttribute(att_name)).isEmpty())
                setId(att_var);
        }
        catch(XMLException ex){
            throw new NCLParsingException("ConnectorBase:\n" + ex.getMessage());
        }
        
        try{
            // create the child nodes
            nl = element.getChildNodes();
            for(int i=0; i < nl.getLength(); i++){
                Node nd = nl.item(i);
                if(nd instanceof Element){
                    Element el = (Element) nl.item(i);

                    //create the imports
                    if(el.getTagName().equals(NCLElementAttributes.IMPORTBASE.toString())){
                        Ei inst = createImportBase();
                        addImportBase(inst);
                        inst.load(el);
                    }
                    // create the connectors
                    if(el.getTagName().equals(NCLElementAttributes.CAUSALCONNECTOR.toString())){
                        Ec inst = createCausalConnector();
                        addCausalConnector(inst);
                        inst.load(el);
                    }
                }
            }
        }
        catch(XMLException ex){
            throw new NCLParsingException("ConnectorBase > " + ex.getMessage());
        }
    }


    public void setModificationListener(NCLModificationListener listener) {
        impl.setModificationListener(listener);
    }


    public NCLModificationListener getModificationListener() {
        return impl.getModificationListener();
    }


    /**
     * Function to create the child element <i>importBase</i>.
     * This function must be overwritten in classes that extends this one.
     *
     * @return
     *          element representing the child <i>importBase</i>.
     */
    protected Ei createImportBase() throws XMLException {
        return (Ei) new NCLImport(NCLImportType.BASE);
    }


    /**
     * Function to create the child element <i>causalConnector</i>.
     * This function must be overwritten in classes that extends this one.
     *
     * @return
     *          element representing the child <i>causalConnector</i>.
     */
    protected Ec createCausalConnector() throws XMLException {
        return (Ec) new NCLCausalConnector();
    }
}
