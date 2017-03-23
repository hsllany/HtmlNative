HtmlNative
========

HtmlNative is a library that parse html (subset) to native widget. It also include Lua Script to manipulate UI widget.

## Example:

HtmlNative will render its view according to a special html file, which contains a special subset of all Html Tag. Even more, you can control the basic logic of this view by writing lua script inside of this html file.

```html
<html>
<body>
    <p id="text1" style="color: #000; font-size: 16; padding: 10">
        This is an example of ImageView
        <br />2 Images are shown.
    </p>
    <img src="http://n.sinaimg.cn/news/crawl/20170302/18ey-fycaahm6004808.jpg" />
    <img src="http://n.sinaimg.cn/news/crawl/20170312/VUL1-fychhvn8494769.jpg" />

    <button onClick="changeText1">clickme</button>
</body>
<script type="text/lua">
    -- show a toast in androd
    alert("hello world")

    -- define a global variable, and set its initial value to false
    b = false

    -- define a function that will change the p#text1 according to b's value.
    function changeText1()
        -- get the v by id
        local v = view("text1")
        if(b) then
            v.style("background:red;color:#fff")
        else
            v.style("background:blue;color:red")
        end

        b = not(b)

    end

    --[[ register the function to callback pool, so the onClick parameter on p will work.]]

    callback("changeText1", changeText1)

</script>
</html>
```

Through HtmlNative, an view like this will be displayed. (All its widget is native widget):

![screen1](show.gif)

## Usage

All you have to do is :

```java

// you can get the raw html file
String htmlStr = ...

HNative.getInstance().loadView(context, mActivity.getAssets().open(fileName), new HNative.OnHNViewLoaded() {

    @Override
    public void onViewLoaded(View v) {
        // do what ever you want to deal with this view
    }

    @Override
    public void onError(Exception e) {
        // if anything went wrong, for example, there is syntax error in html.
    }

    @Override
    public void onHead(HNHead head) {
        // here you can receive the <head> element, deal with title, meta...
    }
});

```

## Currently Supported Tag and Style:

### Common style and parameter of Html Element:

```java

"width": width of this tag, in px, or 100%
"height": width of this tag, in px, or 100%
"background": color of this tag, in string, '#fff', '#ffffff'
"padding": padding, in px
"paddingLeft": in px
"paddingRight": in px
"paddingTop": in px
"paddingBottom": in px
"left": x position for element, only works when parent view is div and its display is "absolute"
"top"; y position for element, only works when parent view is div and its display is "absolute"
"alpha"; in float
"id": string to identify the view
"onClick": name of the function you defined in <script>
"visibility": "visible" or "invisible"
"display": "box", "flex" or "absolute", default is "box"
"direction": only worked when this element is "<div>" and its display is "flex"

```

### Tag


| Tag        | Android Native Widget           | Style  |
| :-------------: |:-------------:|-----|
| <p>      | TextView | color, font-size, line-height, font-style, font-weight, text-align |
| <img>      | ImageView      |   src |
| <iframe> | WebView      |    src |
| <input> | EditText      | same as p |
| <div> | LinearLayout, FlexBoxLayout | display, direction |
| <a> | TextView with click event      | href, same as p |
| <br/> | \n      |  |

### Lua Api

#### alert(msg)

- msg - string, message to show
- Display a toast in Android with msg.

#### view(id)

- id - string
- Return a view by id, may null.

#### view.style(styleString)

- view - return value of view(id)
- styleString - string, such as "background:#fff"
- change the appearance of a view

#### log(msg)
- msg, msg to show in logcat
- show a log in android Logcat with TAG="HNativeLog"

#### callback(functionName, function)
- functionName, register an function with functionName, then you can invoke it in onClick parameter of an Html Element
- function to run


## Future Plan

1. To support more Html Tag, and more Css style
2. Dose not support <style> element yet, will support in future.
3. Will not support <link> element, if you want use this, WebView may a better choice.
4. To support more API.
4. May support javascript script via using V8 engine.

### License

Copyright 2016 Hsllany. All rights reserved.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

