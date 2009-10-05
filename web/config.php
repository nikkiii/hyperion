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
 * Full path to the home directory
 * Don't forget the trailing slash!
 */
define("path_server","/");
/**
 * Full path to the home directory (thru http)
 * Don't forget the trailing slash!
 */
define("path_www","http://www.mywebsite.com/");


// --- NOTHING PAST THIS POINT SHOULD NEED TO BE CHANGED ---

/**
 * Initialize some constants. These are only used internally and should
 * not need to be changed.
 */
define("dir_tpl","tpl/");
define("dir_class","class/");
define("dir_module","module/");
define("dir_req","req/");
define("dir_util","util/");

/**
 * Define priority classes.
 */
define("prio", implode(",",array("")));
?>