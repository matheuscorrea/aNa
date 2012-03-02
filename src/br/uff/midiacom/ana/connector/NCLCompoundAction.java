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
import br.uff.midiacom.ana.datatype.ncl.NCLParsingException;
import br.uff.midiacom.ana.datatype.aux.parameterized.DoubleParamType;
import br.uff.midiacom.ana.datatype.aux.reference.ReferenceType;
import br.uff.midiacom.ana.datatype.enums.NCLActionOperator;
import br.uff.midiacom.ana.datatype.enums.NCLElementAttributes;
import br.uff.midiacom.ana.datatype.ncl.connector.NCLCompoundActionPrototype;
import br.uff.midiacom.ana.reuse.NCLImport;
import br.uff.midiacom.xml.XMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class NCLCompoundAction<T extends NCLCompoundAction,
                               P extends NCLElement,
                               I extends NCLElementImpl,
                               Ea extends NCLAction,
                               Ep extends NCLConnectorParam,
                               Er extends NCLRole,
                               Ip extends NCLImport,
                               R extends ReferenceType<Ea, Ep, Ip>>
        extends NCLCompoundActionPrototype<T, P, I, Ea, Ep, Ip, R>
        implements NCLAction<Ea, P, Ep, Er, Ip, R> {


    public NCLCompoundAction() throws XMLException {
        super();
    }


    @Override
    protected void createImpl() throws XMLException {
        impl = (I) new NCLElementImpl<NCLIdentifiableElement, P>(this);
    }
    
    
    public String parse(int ident) {
        String space, content;

        if(ident < 0)
            ident = 0;

        // Element indentation
        space = "";
        for(int i = 0; i < ident; i++)
            space += "\t";

        content = space + "<compoundAction";
        content += parseAttributes();
        content += ">\n";

        content += parseElements(ident + 1);

        content += space + "</compoundAction>\n";

        return content;
    }
    
    
    protected String parseAttributes() {
        String content = "";
        
        content += parseOperator();
        content += parseDelay();
        
        return content;
    }
    
    
    protected String parseElements(int ident) {
        String content = "";
        
        content += parseActions(ident);
        
        return content;
    }
    
    
    protected String parseOperator() {
        NCLActionOperator aux = getOperator();
        if(aux != null)
            return " operator='" + aux + "'";
        else
            return "";
    }
    
    
    protected String parseDelay() {
        DoubleParamType aux = getDelay();
        if(aux == null)
            return "";
        
        String content = " delay='" + aux.parse();
        if(aux.getValue() != null)
            content += "s'";
        else
            content += "'";
        
        return content;
    }


    protected String parseActions(int ident) {
        if(!hasAction())
            return "";
        
        String content = "";
        for(Ea aux : actions)
            content += aux.parse(ident);
        
        return content;
    }


    public void load(Element element) throws NCLParsingException {
        String att_name, att_var;
        NodeList nl;

        try{
            // set the operator (required)
            att_name = NCLElementAttributes.OPERATOR.toString();
            if(!(att_var = element.getAttribute(att_name)).isEmpty())
                setOperator(NCLActionOperator.getEnumType(att_var));
            else
                throw new NCLParsingException("Could not find " + att_name + " attribute.");

            // set the delay (optional)
            att_name = NCLElementAttributes.DELAY.toString();
            if(!(att_var = element.getAttribute(att_name)).isEmpty())
                setDelay(new DoubleParamType(att_var));
        }
        catch(XMLException ex){
            throw new NCLParsingException("CompoundAction:\n" + ex.getMessage());
        }

        try{
            // create the child nodes
            nl = element.getChildNodes();
            for(int i=0; i < nl.getLength(); i++){
                Node nd = nl.item(i);
                if(nd instanceof Element){
                    Element el = (Element) nl.item(i);

                    //create the simpleAction
                    if(el.getTagName().equals(NCLElementAttributes.SIMPLEACTION.toString())){
                        Ea inst = createSimpleAction();
                        addAction(inst);
                        inst.load(el);
                    }
                    // create the compoundAction
                    if(el.getTagName().equals(NCLElementAttributes.COMPOUNDACTION.toString())){
                        Ea inst = createCompoundAction();
                        addAction(inst);
                        inst.load(el);
                    }
                }
            }
        }
        catch(XMLException ex){
            throw new NCLParsingException("CompoundAction > " + ex.getMessage());
        }
    }
    
    
    public Er findRole(String name) {
        Er result;
        
        for(Ea action : actions){
            result = (Er) action.findRole(name);
            if(result != null)
                return result;
        }
        
        return null;
    }


    /**
     * Function to create the child element <i>simpleAction</i>.
     * This function must be overwritten in classes that extends this one.
     *
     * @return
     *          element representing the child <i>simpleAction</i>.
     */
    protected Ea createSimpleAction() throws XMLException {
        return (Ea) new NCLSimpleAction();
    }


    /**
     * Function to create the child element <i>compoundAction</i>.
     * This function must be overwritten in classes that extends this one.
     *
     * @return
     *          element representing the child <i>compoundAction</i>.
     */
    protected Ea createCompoundAction() throws XMLException {
        return (Ea) new NCLCompoundAction();
    }
}
