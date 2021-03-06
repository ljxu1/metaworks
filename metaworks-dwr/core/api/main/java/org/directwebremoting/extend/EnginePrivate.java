/*
 * Copyright 2005 Joe Walker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.directwebremoting.extend;

import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.util.JavascriptUtil;

/**
 * An abstraction of the dwr.engine Javascript class.
 * @author Joe Walker [joe at getahead dot ltd dot uk]
 */
public class EnginePrivate
{
    /**
     * Begin wrapper with variable alias to do remote calls on the correct DWR instance.
     * @param instanceId DWR instance id from browser
     * @param useWindowParent should this alias target the same window or the parent window?
     * @return JavaScript snippet to be used by other remote calls
     */
    public static String remoteBeginWrapper(String instanceId, boolean useWindowParent)
    {
        StringBuilder buf = new StringBuilder();
        buf.append("(function(){\n");
        if (useWindowParent)
        {
            buf.append("try{\n");
            buf.append("var r=window.parent.dwr._[" + instanceId + "];\n");
        }
        else
        {
            buf.append("var r=window.dwr._[" + instanceId + "];\n");
        }
        return buf.toString();
    }

    /**
     * End wrapper with variable alias to do remote calls on the correct DWR instance.
     * @param instanceId DWR instance id from browser
     * @param useWindowParent should this alias target the same window or the parent window?
     * @return JavaScript snippet to be used by other remote calls
     */
    public static String remoteEndWrapper(String instanceId, boolean useWindowParent)
    {
        StringBuilder buf = new StringBuilder();
        if (useWindowParent)
        {
            buf.append("} catch(ex) {if (!(ex.number && ex.number == -2146823277)) { throw ex; }}\n");
        }
        buf.append("})();\n");
        return buf.toString();
    }

    /**
     * Call the dwr.engine.remote.handleResponse() in the browser
     * @param batchId The identifier of the batch that we are handling a response for
     * @param callId The identifier of the call that we are handling a response for
     * @param data The data to pass to the callback function
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteHandleCallbackScript(String batchId, String callId, Object data)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleCallback", batchId, callId, data);
        return script;
    }

    /**
     * Call dwr.engine.remote.handleException() in the browser
     * @param batchId The identifier of the batch that we are handling a response for
     * @param callId The id of the call we are replying to
     * @param ex The exception to throw on the remote end
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteHandleExceptionScript(String batchId, String callId, Throwable ex)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleException", batchId, callId, ex);
        return script;
    }

    /**
     * Call dwr.engine.remote.handleNewWindowName() in the browser
     * @param windowName The new window name for the page
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteHandleNewWindowNameScript(String windowName)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleNewWindowName", windowName);
        return script;
    }

    /**
     * Call dwr.engine.remote.handleServerException() in the browser
     * @param batchId The identifier of the batch that we are handling a response for
     * @param ex The exception from which we make a reply
     * @return The script to send to the browser
     */
    public static String getRemoteHandleBatchExceptionScript(String batchId, Exception ex)
    {
        StringBuffer reply = new StringBuffer();

        String output = JavascriptUtil.escapeJavaScript(ex.getMessage());
        String params = "{ name:'" + ex.getClass().getName() + "', message:'" + output + "' }";
        if (batchId != null)
        {
            params += ", '" + batchId + "'";
        }

        reply.append(ProtocolConstants.SCRIPT_CALL_REPLY).append("\r\n");
        reply.append("r.handleBatchException(").append(params).append(");\r\n");

        return reply.toString();
    }

    /**
     * Call dwr.engine.remote.executeFunction() in the browser
     * @param id The registered function name
     * @param params The data to pass to the function
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteExecuteFunctionScript(String id, Object[] params)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleFunctionCall", id, params);
        return script;
    }

    /**
     * Call dwr.engine.remote.executeFunction() in the browser
     * @param id The registered function name
     * @param params The data to pass to the function
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteExecuteObjectScript(String id, String methodName, Object[] params)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleObjectCall", id, methodName, params);
        return script;
    }

    /**
     * Call dwr.engine.remote.executeFunction() in the browser
     * @param id The registered function name
     * @param propertyName The name of the property to alter on the client object
     * @param data The new value for the client object property
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteSetObjectScript(String id, String propertyName, Object data)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleSetCall", id, propertyName, data);
        return script;
    }

    /**
     * Call dwr.engine.remote.closeFunction() in the browser
     * @param id The registered function name
     * @return The script to send to the browser
     */
    public static ScriptBuffer getRemoteCloseFunctionScript(String id)
    {
        ScriptBuffer script = new ScriptBuffer();
        script.appendCall("r.handleFunctionClose", id);
        return script;
    }

    /**
     * Call dwr.engine.remote.pollCometDisabled() in the browser
     * @param batchId The identifier of the batch that we are handling a response for
     * @return The script to send to the browser
     */
    public static String getRemotePollCometDisabledScript(String batchId)
    {
        StringBuffer reply = new StringBuffer();

        String params = "{ name:'dwr.engine.pollAndCometDisabled', message:'Polling and Comet are disabled. See the server logs.' }";
        if (batchId != null)
        {
            params += ", '" + batchId + "'";
        }

        reply.append(ProtocolConstants.SCRIPT_CALL_REPLY).append("\r\n");
        reply.append("r.pollCometDisabled(").append(params).append(");\r\n");

        return reply.toString();
    }

    /**
     * Returns the name of the newObject function.
     */
    public static String remoteNewObjectFunction()
    {
        return "r.newObject";
    }

    /**
     * Take an XML string, and convert it into some Javascript that when
     * executed will return a DOM object that represents the same XML object
     * @param xml The XML string to convert
     * @return The Javascript
     */
    public static String xmlStringToJavascriptDomElement(String xml)
    {
        String xmlout = JavascriptUtil.escapeJavaScript(xml);
        return "r.toDomElement(\"" + xmlout + "\")";
    }

    /**
     * Take an XML string, and convert it into some Javascript that when
     * executed will return a DOM object that represents the same XML object
     * @param xml The XML string to convert
     * @return The Javascript
     */
    public static String xmlStringToJavascriptDomDocument(String xml)
    {
        String xmlout = JavascriptUtil.escapeJavaScript(xml);
        return "r.toDomDocument(\"" + xmlout + "\")";
    }

    /**
     * Get a string which will initialize a dwr.engine object
     * @return A dwr.engine init script
     */
    public static String getRequireEngineScript()
    {
        return "if (typeof dwr == 'undefined' || dwr.engine == undefined) throw new Error('You must include DWR engine before including this file');\n";
    }

    /**
     * The DefaultRemoter needs to know the name of the execute function
     * @return The execute function name
     */
    public static String getExecuteFunctionName()
    {
        return "dwr.engine._execute";
    }

    /**
     * A script to send at the beginning of an iframe response
     * @param batchId The id of the current batch
     * @param useWindowParent Will the exec happen from a child iframe which is
     * the case for normal iframe based calls, or from the main window, which is
     * the case for iframe streamed polling.
     * @return A script to init the environment
     */
    public static String remoteBeginIFrameResponse(String batchId, boolean useWindowParent)
    {
        return "r.beginIFrameResponse(this.frameElement"+(batchId == null?"":", '" + batchId+"'") + ");";
    }

    /**
     * A script to send at the end of an iframe response
     * @param batchId The id of the current batch
     * @param useWindowParent Will the exec happen from a child iframe which is
     * the case for normal iframe based calls, or from the main window, which is
     * the case for iframe streamed polling.
     * @return A script to tidy up the environment
     */
    public static String remoteEndIFrameResponse(String batchId, boolean useWindowParent)
    {
        return "r.endIFrameResponse("+(batchId == null?"":"'" + batchId+"'")+");";
    }

    /**
     * Prepare a script for execution in an iframe environment
     * @param script The script to modify
     * @return The modified script
     */
    public static String remoteEval(String script)
    {
    	// We need to define the "r" redirector variable inside the eval as well
    	// as it can't access the global "r" defined in the response closure.
        return "r._eval(\"var r= dwr._ ? dwr._[dwr.engine._instanceId] : window.dwr._[dwr.engine._instanceId];" + JavascriptUtil.escapeJavaScript(script) + "\");";
    }

    /**
     * Evade the 2 connection limit by sending scripts to the wrong window and
     * having that use window.name to push it to the right window
     * @param original The script that we wish to be proxied
     * @param windowName The window to which we wish the window to be proxied
     * @return A script to send to a different window to cause proxying
     */
    public static ScriptBuffer createForeignWindowProxy(String windowName, ScriptBuffer original)
    {
        String proxy = JavascriptUtil.escapeJavaScript(original.toString());

        ScriptBuffer reply = new ScriptBuffer();
        reply.appendCall("r.handleForeign", windowName, proxy);
        reply.appendData(proxy);
        return reply;
    }
}
