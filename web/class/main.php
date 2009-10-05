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

require("../config.php");

/**
 * Main class that initializes all other classes
 *
 */
class Hyperite
{
	// __construct method is called when the object is initialized
	public function __construct()
	{
		session_start();
		$prio = explode(",",prio);
		// load and initialize all priority classes or die on error
		foreach($prio as $curr)
		{
			require(path_server.dir_class.dir_req.$curr.".php");
			if(class_exists($curr))
			{
				$this->$curr = new $curr();
				// bind that object to this one
				$this->$curr->Hyperite = &$this;
				// initialize if necessary
				if(method_exists($this->$curr, 'init')) $this->$curr->init();
			} else die("<strong>Fatal error:</strong> Required module class '$curr' failed to load.");
		}
	}
	
	// load a given module
	public function loadModule($module)
	{
			require(path_server.dir_module.$module."/".$module.".php");
			if(class_exists($module))
			{
				$this->$module = new $module();
				$this->$module->Hyperite = &$this;
				if(method_exists($this->$module, 'init')) $this->$module->init();
			} else echo("<strong>Warning:</strong> Could not initiate module ".$module);			
	}
	
	// array search method
	public function arrayMatch($needle, $array_stack)
	{
		foreach($array_stack as $curr)
		{
			if($curr == $needle) return true;
		}
		return false;
	}
}
$hyperite = new Hyperite;
?>