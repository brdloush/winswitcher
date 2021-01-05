# winswitcher

`winswitcher` is a simple utility that allows you to pre-define "screen layouts" of windows which can then be easily restored back in its original "shape". The application windows must already be running, this project has no ambition in starting any new application to reach the desired state. It's just re-shuffling existing windows, trying to make life a bit more bearable.

## Motivation

When I'm working, I tend to be messy. When I'm not careful, I easily end up with a collection of many open application windows. And even 5-10 windows is often a way too many windows for effective switching between them. 

Some people use things like virtual desktops or tiling desktop managers. Indeed, they help with finding at least some inner balance in such chaotic process of window management. Although I understand the benefits of virtual desktops, I never really got to like them and use them on daily bases. Old habits die hard. All I want to be able to do is to quickly re-shuffle those few windows I consider important. On my single desktop.

For example, imagine you're building some web application. You edit your code in editor (let's say VS Code) and whenever you make a change, you want to see the change being auto-magically applied in your browser (let's say Chrome with auto-reload). Therefore your primary layout will be:

- `code.8,chrome.4`, bound to key combination of `Super+1` (`Super` key is the `Windows` key). No matter what messy situation I'm currently in, when I press `Super+1`, the `winswitcher` utility will try to find the most recently used "editor" window (vs code in our case my case) and it resize & place it on the leftmost 8/12 part of my screen. Then it will find the most currently used browser (Chrome in my case) and place it into remaining 4/12 mof the screen.

Cool. I end up with larger `VS Code` on the left and smaller `Chrome` on the right of my screen. This allows me to work efficiently as I can make changes in my editor and immediately see the effect of my change in small preview chrome window.

But sometimes the "preview" 4/12 `chrome` window is too small and I need to quickly see it in maximized view. That's when `chrome.1` layout comes in place, which I can quickly apply using `Super+2`. It finds the most recently used chrome window and fully maximizes it on my screen.

But hey, in other cases I don't even need to see the `chrome`  window at all. All I want to do is to fully focus on code editing. You guessed it, `code.1` layout is there, ready to be activated using `Super+3`.

I also often need to bring up fully maximized `slack` window, as I just that someone wrote some message to me. Yes, `slack.1` layout is ready to be activated using `Super+4`.

Now I can quickly bring up 4 combos:
- `Super+1`: large `vs code`+ small `chrome`
- `Super+2`: maximized `chrome`
- `Super+3`: maximized `vs code`
- `Super+4`: maximize `slack`

For me, this setup helps a lot with avoiding `alt-tab` window juggling/re-focusing.

## Installation

Simply copy or symlink `winswitcher.clj` to `~/.local/bin` or similar directory on your path and change it to be executable. For example:

```bash
cp ./src/winswitcher.clj ~/bin/winswitcher && chmod +x ~/bin/winswitcher 
```

Also make sure that all the required dependencies are installed on your system.

## Dependencies

In order to run `winswitcher` you need to be running linux and have following CLI utilities installed:

- `babashka` - which is a lovely, fast, natively compiled clojure interpreter. `winswitcher` is written in clojure/babashka, because it's easy, interactive and fun. Give it a try if you haven't done so yet!
- `xdotool`
- `wmctrl`
- `xrandr`
- `bash`
- `grep`

Yeah, some of the tools (`bash` and `grep`) may not really be needed. But hey, everybody has them. So let's not worry about being a bit lazy here.

## Running winswitcher

`winswitcher` is a simple CLI-friendly script that only accepts command-line argument with definition of desired layout. 

Here's a few examples:

```bash
# from left to right: chrome (1/10 of screen), firefox (2/10 of screen), vs code (3/10 of screen), terminator (4/10 of screen)
winswitcher chrome.1,firefox.2,code.3,terminator.4

# just Microsoft teams
winswitcher teams.1
```
## What class name should I use as an argument?

Either you can try to guess the correct value for app (usually the executable name works: Visual Studio Code = `code`) or you can execute the `xprop` tool from your shell and click the application window.

```bash
➜  ~ xprop | grep CLASS 
# .. then I clicked my terminator (terminal emulator) app
WM_CLASS(STRING) = "terminator", "Terminator"

➜  ~ xprop | grep CLASS
# .. then I clicked my Libre-office Writer app
WM_CLASS(STRING) = "libreoffice", "libreoffice-writer"

➜  ~ xprop | grep CLASS
# .. then I clicked my Libre-office Calc app
WM_CLASS(STRING) = "libreoffice", "libreoffice-calc"
```
## License

Copyright © 2021 Tomas Brejla

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
