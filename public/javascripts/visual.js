var eidtor = null;

function initEditor() {
	editor = ace.edit("editor");
	editor.setTheme("ace/theme/twilight");
}

var editor0 = null;

function initEditor0() {
	editor0 = ace.edit("editor0");
	editor0.setTheme("ace/theme/twilight");
}

function doGet(_url) {
	var request = $.ajax({
		  url: _url,
		  method: "GET"
		});
		 
	request.done(function(msg) {
		editor0.setValue(msg);
	});
	
	request.fail(function( jqXHR, textStatus ) {
		editor0.setValue("error!");
	});
}

function lex() {
	var source = editor.getSession().getValue();
	doGet("lex?source="+source);
}

function parse() {
	var source = editor.getSession().getValue();
	doGet("parse?source="+source);
}

function ir() {
	var source = editor.getSession().getValue();
	doGet("ir?source="+source);
}

function target() {
	var source = editor.getSession().getValue();
	doGet("target?source="+source);
}

function exec() {
	var source = editor.getSession().getValue();
	doGet("exec?source="+source);
}