package com.dam.coach.flow;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dam.coach.model.entity.CoachAction;
import com.dam.coach.store.CoachActionStore;

@Component
public class FlowGenerator {
	
	@Autowired
	CoachActionStore coachActionStore;
	
	private final static String URI_FLOW = "https://investmal.de:6250/coach/getFlow?actionReference";
	
	private final static String URI_MSG = "https://investmal.de:6250/coach/getActionReplaced?actionReference";
	
	public String getHtml(String actionReference, String tokenId) {
		return HTML_START + generateHtml(actionReference, tokenId) + HTML_END;
	}
	
	private String generateHtml(String actionReference, String tokenId) {
		String flowBody = "";
		CoachAction action = coachActionStore.getRawActionByReference(actionReference);
		flowBody+= "st=>start: " + action.getActionReference() + "\n";
		flowBody+= "e=>end:>http://www.google.com\n";
		flowBody+= "investorText=>operation: Investor: " + action.getText() + "\n";
		flowBody+= "btn=>operation: Button: " + action.getBtnText() + "\n";
		flowBody+= "msg=>subroutine: Coach: " + action.getMessage() + ":>" + URI_MSG + "=" + action.getActionReference()  + "\n";
		flowBody+= "cond0=>condition: options=0\n";

		Iterator<String> options = action.getOptionList().iterator();
		int index = 0;
		while (options.hasNext()) {
			String option = options.next();
			flowBody+= "sub" + String.valueOf(index+1)+ "=>subroutine: "+ option + ":>" + URI_FLOW + "=" + option + "&tokenId=" + tokenId + "\n";
			
			flowBody+= "cond" + String.valueOf(index+1)+ "=>condition: options=" + String.valueOf(index+1) + "\n";
			index++;
		}
		
		flowBody+= "st->investorText->btn->msg->cond0\n";
		flowBody+= "cond0(yes)->e\n";
		
		options = action.getOptionList().iterator();
		index = 0;
		while (options.hasNext()) {
			options.next();
			if (index < action.getOptionList().size()) {
				flowBody+= "cond" + String.valueOf(index) + "(no)->cond" + String.valueOf(index+1) + "\n";
			}
			if (index == action.getOptionList().size()) {
				flowBody+= "cond" + String.valueOf(index) + "(no)->e" + String.valueOf(index+1) + "\n";
			}

			flowBody+= "cond" + String.valueOf(index+1) + "(yes)->sub" + String.valueOf(index+1) +  "\n";
			flowBody+= "sub" + String.valueOf(index+1) + "(right)->e\n";
		}

		return flowBody;
	}
	
	private String HTML_START = "<!DOCTYPE html>\n" + 
			"<html lang=\"en\">\n" + 
			"    <head>\n" + 
			"        <meta charset=\"utf-8\">\n" + 
			"        <title>investmal Coach Dialog</title>\n" + 
			"        <style type=\"text/css\">\n" + 
			"          .end-element { fill : #FFCCFF; }\n" + 
			"        </style>\n" + 
			"        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/raphael/2.2.0/raphael-min.js\"></script>\n" + 
			"        <script src=\"https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.0/jquery.min.js\"></script>\n" + 
			"        <script src=\"https://flowchart.js.org/flowchart-latest.js\"></script>\n" + 
			"        <!-- <script src=\"../release/flowchart.min.js\"></script> -->\n" + 
			"        <script>\n" + 
			"            window.onload = function () {\n" + 
			"                var btn = document.getElementById(\"run\"),\n" + 
			"                    cd = document.getElementById(\"code\"),\n" + 
			"                    chart;\n" + 
			"                (btn.onclick = function () {\n" + 
			"                    var code = cd.value;\n" + 
			"                    if (chart) {\n" + 
			"                      chart.clean();\n" + 
			"                    }\n" + 
			"                    chart = flowchart.parse(code);\n" + 
			"                    chart.drawSVG('canvas', {\n" + 
			"                      // 'x': 30,\n" + 
			"                      // 'y': 50,\n" + 
			"                      'line-width': 3,\n" + 
			"                      'maxWidth': 3,//ensures the flowcharts fits within a certian width\n" + 
			"                      'line-length': 50,\n" + 
			"                      'text-margin': 10,\n" + 
			"                      'font-size': 14,\n" + 
			"                      'font': 'normal',\n" + 
			"                      'font-family': 'Helvetica',\n" + 
			"                      'font-weight': 'normal',\n" + 
			"                      'font-color': 'black',\n" + 
			"                      'line-color': 'black',\n" + 
			"                      'element-color': 'black',\n" + 
			"                      'fill': 'white',\n" + 
			"                      'yes-text': 'yes',\n" + 
			"                      'no-text': 'no',\n" + 
			"                      'arrow-end': 'block',\n" + 
			"                      'scale': 1,\n" + 
			"                      'symbols': {\n" + 
			"                        'start': {\n" + 
			"                          'font-color': 'red',\n" + 
			"                          'element-color': 'green',\n" + 
			"                          'fill': 'yellow'\n" + 
			"                        },\n" + 
			"                        'end':{\n" + 
			"                          'class': 'end-element'\n" + 
			"                        }\n" + 
			"                      },\n" + 
			"                      'flowstate' : {\n" + 
			"                        'past' : { 'fill' : '#CCCCCC', 'font-size' : 12},\n" + 
			"                        'current' : {'fill' : 'yellow', 'font-color' : 'red', 'font-weight' : 'bold'},\n" + 
			"                        'future' : { 'fill' : '#FFFF99'},\n" + 
			"                        'request' : { 'fill' : 'blue'},\n" + 
			"                        'invalid': {'fill' : '#444444'},\n" + 
			"                        'approved' : { 'fill' : '#58C4A3', 'font-size' : 12, 'yes-text' : 'APPROVED', 'no-text' : 'n/a' },\n" + 
			"                        'rejected' : { 'fill' : '#C45879', 'font-size' : 12, 'yes-text' : 'n/a', 'no-text' : 'REJECTED' }\n" + 
			"                      }\n" + 
			"                    });\n" + 
			"                    $('[id^=sub1]').click(function(){\n" + 
//			"                      alert('info here');\n" + 
			"                    });\n" + 
			"                })();\n" + 
			"            };\n" + 
			"            function myFunction(event, node) {\n" + 
			"              console.log(\"You just clicked this node:\", node);\n" + 
			"            }\n" + 
			"            \n" + 
			"        </script>\n" + 
			"    </head>\n" + 
			"    <body>\n" + 
			"        <div><textarea id=\"code\" style=\"width: 100%;\" rows=\"11\">\n";

			
	private String HTML_END = "	</textarea></div>\n" + 
			"        <div><button id=\"run\" type=\"button\">Run</button></div>\n" + 
			"        <div id=\"canvas\"></div>\n" + 
			"    </body>\n" + 
			"</html>\n" + 
			"";
	

}
