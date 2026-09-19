cmake_minimum_required(VERSION 3.22.1)
project(containment_probe LANGUAGES C)
add_library(containment_probe SHARED probe.c)
target_compile_options(containment_probe PRIVATE -Wall -Wextra -Werror)
