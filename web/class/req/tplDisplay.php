<?php
/**
 * Copyright (c) 2009 Graham Edgecombe, Blake Beaupain and Brett Russell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

/**
 * Layout class, handles anything that has to do with the site's looks. Pretty basic, but very extendable.
 * I have to actually finish it though :D
 */
class layout
{
	public $tpl;
	public $content;
	public $title;
	public $head;
	
	public function init() { /* nothing to init */ }
	
	protected function standarize()
	{
		
		if(!is_array($this->content)) { die("Array expected as content. Type was instead ".gettype($this->content)); }
		$this->title = (string) $this->title;
		$this->tpl = (string) $this->tpl;
	}
	
	protected function getMenu()
	{
		// todo
	}
	
	protected function getTemplateSet($forceTemplate = "default")
	{
		// todo
		// gets template set to use from config somewhere (TBD)
		// will also allow for forcing template sets on specific pages
		$forceTemplate .= "/";
		return $forceTemplate;
	}
	
	public function show($costumtpl = null)
	{
		$this->standarize();
		if($costumtpl == null)
		{
			
			$section = file_get_contents(path_server.dir_tpl.$this->getTemplateSet()."section.tpl");
			$content = "";
			foreach($this->content as $currcontent)
			{
				$content .= str_replace(array("{title}","{content}"),array($currcontent[0],$currcontent[1]),$section);
			}
		}
		else
		{
			$content = "";
			$section = file_get_contents(path_server.dir_tpl.$this->getTemplateSet().$costumtpl);
			foreach($this->content as $currvar)
			{
				$content .= $section;
				foreach($currvar as $currname => $currcontent)
				{
					$content = str_replace("{".$currname."}",$currcontent,$content);
				}
			}
		}
		$main = file_get_contents(path_server.dir_tpl.$this->getTemplateSet()."main.tpl");
		die(str_replace(array("{http_url}","{head}","{title}","{greeting}","{main}","{footer}","{header}"),array(path_www, $this->head, $this->title, $this->greeting, $content, $this->footer, $this->header),str_replace("{menu}",$this->getMenu(),$main)));	
	}
}
?>