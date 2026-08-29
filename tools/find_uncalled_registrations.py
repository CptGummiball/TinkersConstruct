"""Finds public static init/setup/register() methods that nothing calls.

A registration method nobody invokes is the recurring failure of this port: it compiles, it looks
done, and the feature is simply absent at runtime.
"""
import os
import re

ROOT = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), 'src', 'main', 'java')

decl = re.compile(r'public static void (init|setup|register)\(\)')
sources = {}
for dp, _d, fs in os.walk(ROOT):
    for f in fs:
        if f.endswith('.java'):
            full = os.path.join(dp, f)
            with open(full, encoding='utf-8', errors='replace') as fh:
                sources[full] = fh.read()

blob = '\n'.join(sources.values())
for full, text in sorted(sources.items()):
    name = os.path.basename(full)[:-5]
    for method in sorted(set(decl.findall(text))):
        call = name + '.' + method + '()'
        # count calls outside the declaring file
        others = blob.count(call) - text.count(call)
        if others == 0:
            rel = full.replace(ROOT + os.sep, '').replace(os.sep, '/')
            print('UNCALLED  %-28s %s' % (call, rel))
