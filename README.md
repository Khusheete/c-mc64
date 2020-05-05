This is a compiler and interpreter for NOPEname's virtual computer made in minecraft named "MC64".
NOPEname's youtube channel: https://www.youtube.com/channel/UC7qxEChJNOMVrfNRKU1mw6g


# usage:

mc64 [file] [args]


# command arguments

-mode=(interpret|compile|verify)
    change the mode: interpret (default) - interpret the given program (will verify the program)
                     compile - compiles the given program to a .mcfunction file (so we can run it on Minecraft) (will verify the program)
                     verify - verifies the given program
    /!\ for now only "interpret" and "verify" are working

-out=(file)
    sets the output file (default: ./prog.mcfunction) if there is no .mcfunction at the end, it will be automaticly append
