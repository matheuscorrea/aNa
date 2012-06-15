/********************************************************************************
 * This file is part of the API for NCL Authoring - aNa.
 *
 * Copyright (c) 2011, MidiaCom Lab (www.midiacom.uff.br)
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
 *    display the following acknowledgment:
 *        This product includes the API for NCL Authoring - aNa
 *        (http://joeldossantos.github.com/aNa).
 *
 *  * Neither the name of the lab nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without specific
 *    prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY MIDIACOM LAB AND CONTRIBUTORS ``AS IS'' AND
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
package br.uff.midiacom.ana.descriptor;

import br.uff.midiacom.ana.NCLElement;
import br.uff.midiacom.ana.util.exception.XMLException;
import br.uff.midiacom.ana.util.ncl.NCLBindConstituent;


/**
 * Interface that represents a layout descriptor. A layout descriptor can be a
 * descriptor or a descriptor switch element.
 * 
 * @param <T>
 * @param <P> 
 */
public interface NCLLayoutDescriptor<T extends NCLElement,
                                     El extends NCLLayoutDescriptor>
        extends NCLElement<T>, NCLBindConstituent<T> {

    
    /**
     * Searches for a descriptor inside a descriptorSwitch and its descendants.
     * The descriptor can be a descriptor or a descriptorSwitch.
     * 
     * @param id
     *          id of the descriptor to be found.
     * @return 
     *          descriptor or null if no descriptor was found.
     */
    public El findDescriptor(String id) throws XMLException;
    
    
    /**
     * Searches for a descriptor inside a descriptorSwitch and its descendants.
     * 
     * @param focusIndex
     *          integer or string of the descriptor to be found.
     * @return 
     *          descriptor or null if no descriptor was found.
     */
    public El findDescriptor(Object focusIndex) throws XMLException;
}
