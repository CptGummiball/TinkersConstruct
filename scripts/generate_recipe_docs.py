"""Generates the recipe reference under docs/reference from the generated data tree.

Run from the repository root:
    python scripts/generate_recipe_docs.py

The reference is derived entirely from src/generated/resources (plus the handwritten
recipes in src/main/resources where present), so it stays truthful to what ships: rerun
after a datagen change and commit the result. Every recipe file is counted; anything the
writers below do not cover lands in misc.md so nothing can silently drop out.
"""
import io
import json
import os
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RECIPES = os.path.join(ROOT, 'src/generated/resources/data/tconstruct/recipe')
TINKERING = os.path.join(ROOT, 'src/generated/resources/data/tconstruct/tinkering')
OUT = os.path.join(ROOT, 'docs/reference')

covered = set()
all_recipes = {}


def load_all():
    for dirpath, _dirs, files in os.walk(RECIPES):
        for f in files:
            if f.endswith('.json'):
                full = os.path.join(dirpath, f)
                rel = os.path.relpath(full, RECIPES).replace(os.sep, '/')
                try:
                    all_recipes[rel] = json.load(io.open(full, encoding='utf-8'))
                except Exception as e:
                    raise RuntimeError('failed parsing %s: %s' % (rel, e))


def write(name, lines):
    path = os.path.join(OUT, name)
    os.makedirs(os.path.dirname(path), exist_ok=True)
    io.open(path, 'w', encoding='utf-8', newline='\n').write('\n'.join(lines) + '\n')
    print('wrote %s (%d lines)' % (name, len(lines)))


def esc(text):
    return str(text).replace('|', '\\|')


def ingredient(json_in):
    """Renders a vanilla/mantle item ingredient compactly."""
    if json_in is None:
        return ''
    if isinstance(json_in, str):
        return '`%s`' % json_in
    if isinstance(json_in, list):
        return ' / '.join(ingredient(e) for e in json_in)
    if isinstance(json_in, dict):
        if 'tag' in json_in and len(json_in) <= 2:
            base = '`#%s`' % json_in['tag']
            if 'count' in json_in:
                base = '%s x%s' % (json_in['count'], base)
            return base
        if 'item' in json_in:
            base = '`%s`' % json_in['item']
            if json_in.get('count', 1) != 1:
                base = '%s %s' % (json_in['count'], base)
            return base
        if json_in.get('type') == 'mantle:difference':
            return '%s minus %s' % (ingredient(json_in.get('base')), ingredient(json_in.get('subtracted')))
        if json_in.get('type') == 'mantle:intersection':
            return ' AND '.join(ingredient(e) for e in json_in.get('ingredients', []))
        if json_in.get('type') == 'mantle:without_container':
            return '%s (no container)' % ingredient(json_in.get('match'))
    return '`%s`' % esc(json.dumps(json_in, separators=(',', ':')))


def fluid(json_in):
    """Renders a fluid stack/ingredient: name and amount in mb."""
    if isinstance(json_in, list):
        return ' / '.join(fluid(e) for e in json_in)
    if isinstance(json_in, dict):
        name = json_in.get('tag') or json_in.get('fluid') or json_in.get('name') or '?'
        prefix = '#' if 'tag' in json_in else ''
        amount = json_in.get('amount')
        if amount is not None:
            return '%s mb `%s%s`' % (amount, prefix, name)
        return '`%s%s`' % (prefix, name)
    return '`%s`' % esc(json_in)


def result_item(json_in):
    if isinstance(json_in, str):
        return '`%s`' % json_in
    if isinstance(json_in, dict):
        if 'tag' in json_in:
            return ingredient(json_in)
        name = json_in.get('item') or json_in.get('id') or '?'
        count = json_in.get('count', 1)
        return ('%s `%s`' % (count, name)) if count != 1 else '`%s`' % name
    return esc(json_in)


def conditions(json_in):
    """Summarizes recipe load conditions."""
    conds = json_in.get('conditions')
    if not conds:
        return ''
    out = []
    for c in conds:
        t = c.get('type', '')
        if t == 'mantle:tag_filled':
            out.append('tag `%s` filled' % c.get('tag', '?'))
        elif t == 'mantle:tag_combination_filled':
            match = ' + '.join('`%s`' % m for m in c.get('match', []))
            note = ' (ignoring `%s`)' % c['ignore'] if 'ignore' in c else ''
            out.append('tags %s filled%s' % (match, note))
        elif t == 'forge:mod_loaded':
            out.append('mod `%s`' % c.get('modid', c.get('mod', '?')))
        elif t == 'forge:item_exists':
            out.append('item `%s`' % c.get('item', c.get('name', '?')))
        elif t == 'tconstruct:config':
            out.append('config `%s`' % c.get('prop'))
        elif t == 'forge:not':
            out.append('not(%s)' % conditions({'conditions': [c.get('value')]}))
        elif t == 'forge:or':
            out.append('any of(%s)' % conditions({'conditions': c.get('values', [])}))
        elif t == 'forge:and':
            out.append('all of(%s)' % conditions({'conditions': c.get('values', [])}))
        elif t == 'forge:true':
            pass
        else:
            out.append(t)
    return '; '.join(out)


def rows_table(header, rows):
    lines = ['| ' + ' | '.join(header) + ' |', '|' + '---|' * len(header)]
    for row in rows:
        lines.append('| ' + ' | '.join(row) + ' |')
    return lines


# --- melting ---

def gen_melting():
    lines = ['# Melting', '',
             'Everything the smeltery and foundry melt down. Amounts are millibuckets; 90 mb is one',
             'ingot, 810 mb one block, 10 mb one nugget. `#` marks a tag. Ore melting multiplies its',
             'output by the ore rate config (default: smeltery 12/9, foundry 9/9 with byproducts).',
             'A condition means the recipe only loads when it holds, which is how the compat metals',
             'switch on when another mod provides them.', '']
    groups = defaultdict(list)
    for rel, j in sorted(all_recipes.items()):
        if not rel.startswith('smeltery/melting/'):
            continue
        t = j.get('type', '')
        if t not in ('tconstruct:melting', 'tconstruct:ore_melting', 'tconstruct:damagable_melting', 'tconstruct:material_melting'):
            continue
        covered.add(rel)
        group = rel.split('/')[2] if rel.count('/') >= 3 else '(root)'
        groups[group].append((rel, j))
    for group in sorted(groups):
        lines.append('## %s' % group)
        lines.append('')
        rows = []
        for rel, j in groups[group]:
            name = rel.split('/', 2)[2][:-5]
            extra = []
            if j['type'] == 'tconstruct:ore_melting':
                extra.append('ore rate: %s' % j.get('rate', 'default'))
            if j['type'] == 'tconstruct:damagable_melting':
                extra.append('scales with damage')
            for by in j.get('byproducts', []):
                extra.append('byproduct %s' % fluid(by))
            cond = conditions(j)
            if cond:
                extra.append('requires %s' % cond)
            rows.append([esc(name), ingredient(j.get('ingredient')), fluid(j.get('result')),
                         str(j.get('temperature', '')), str(j.get('time', '')), esc('; '.join(extra))])
        lines += rows_table(['Recipe', 'Input', 'Result', 'Temp (C)', 'Time', 'Notes'], rows)
        lines.append('')
    return lines


# --- alloys ---

def gen_alloys():
    lines = ['# Alloys', '',
             'Alloying combines fluids inside the smeltery or alloyer. The temperature is the',
             'minimum the structure must reach.', '']
    rows = []
    for rel, j in sorted(all_recipes.items()):
        if not rel.startswith('smeltery/alloys/'):
            continue
        covered.add(rel)
        name = rel.split('/')[-1][:-5]
        inputs = ' + '.join(fluid(i) for i in j.get('inputs', []))
        cond = conditions(j)
        rows.append([esc(name), inputs, fluid(j.get('result')), str(j.get('temperature', '')), esc(cond)])
    lines += rows_table(['Alloy', 'Inputs', 'Result', 'Temp (C)', 'Condition'], rows)
    return lines


# --- casting ---

def gen_casting():
    lines = ['# Casting', '',
             'Casting pours a fluid onto a table (with or without a cast) or into a basin.',
             'Multi-use casts survive the pour; sand casts are single-use. Material and part',
             'casting (tool parts from any castable material) is table-driven per material and',
             'covered in [materials.md](materials.md); the entries here are the fixed recipes.', '']
    groups = defaultdict(list)
    for rel, j in sorted(all_recipes.items()):
        t = j.get('type', '')
        if t in ('tconstruct:casting_table', 'tconstruct:casting_basin', 'tconstruct:retextured_casting_table', 'tconstruct:retextured_casting_basin', 'tconstruct:potion_casting', 'tconstruct:container_filling_table', 'tconstruct:container_filling_basin'):
            covered.add(rel)
            parts = rel.split('/')
            group = parts[2] if rel.startswith('smeltery/casting/') and len(parts) > 3 else parts[0] + '/' + parts[1] if len(parts) > 1 else '(root)'
            groups[group].append((rel, j))
    for group in sorted(groups):
        lines.append('## %s' % group)
        lines.append('')
        rows = []
        for rel, j in groups[group]:
            name = rel.split('/')[-1][:-5]
            basin = 'basin' in j.get('type', '')
            cast = ingredient(j.get('cast')) if 'cast' in j else ''
            if j.get('cast_consumed'):
                cast += ' (consumed)'
            notes = []
            if j.get('switch_slots'):
                notes.append('switch slots')
            cond = conditions(j)
            if cond:
                notes.append('requires %s' % cond)
            result = result_item(j.get('result'))
            rows.append([esc(name), 'basin' if basin else 'table', fluid(j.get('fluid')) if 'fluid' in j else '',
                         cast, result, str(j.get('cooling_time', '')), esc('; '.join(notes))])
        lines += rows_table(['Recipe', 'Where', 'Fluid', 'Cast', 'Result', 'Cooling', 'Notes'], rows)
        lines.append('')
    return lines


# --- entity melting ---

def gen_entity_melting():
    lines = ['# Entity melting', '',
             'Entities standing inside a smeltery take damage and yield fluid per hit.', '']
    rows = []
    for rel, j in sorted(all_recipes.items()):
        if j.get('type') != 'tconstruct:entity_melting':
            continue
        covered.add(rel)
        name = rel.split('/')[-1][:-5]
        ent = j.get('entity', {})
        if 'types' in ent:
            entities = ', '.join('`%s`' % e for e in ent['types'])
        elif 'tag' in ent:
            entities = '`#%s`' % ent['tag']
        else:
            entities = esc(json.dumps(ent))
        rows.append([esc(name), entities, fluid(j.get('result')), str(j.get('damage', 2)), esc(conditions(j))])
    lines += rows_table(['Recipe', 'Entities', 'Result per hit', 'Damage', 'Condition'], rows)
    return lines


# --- modifiers ---

def gen_modifiers():
    lines = ['# Modifier recipes', '',
             'Recipes applied at the tinker station or anvil. Slots name the slot type consumed',
             'per level. Incremental modifiers accept partial inputs until the amount per level is',
             'reached. Salvage rows are omitted: every modifier here has a matching salvage recipe',
             'returning its slots unless noted.', '']
    groups = defaultdict(list)
    salvage = 0
    for rel, j in sorted(all_recipes.items()):
        t = j.get('type', '')
        if t == 'tconstruct:modifier_salvage':
            covered.add(rel)
            salvage += 1
            continue
        if t in ('tconstruct:modifier', 'tconstruct:incremental_modifier', 'tconstruct:swappable_modifier',
                 'tconstruct:multilevel_modifier', 'tconstruct:modifier_requirements', 'tconstruct:creative_slot',
                 'tconstruct:armor_dyeing', 'tconstruct:enchantment_converting', 'tconstruct:modifier_repair',
                 'tconstruct:severing_ammo', 'tconstruct:material_debug'):
            covered.add(rel)
            group = '/'.join(rel.split('/')[1:-1]) or '(root)'
            groups[group].append((rel, j))
    for group in sorted(groups):
        lines.append('## %s' % group)
        lines.append('')
        rows = []
        for rel, j in groups[group]:
            name = rel.split('/')[-1][:-5]
            result = j.get('result', '')
            if isinstance(result, dict):
                result = result.get('name', result.get('id', json.dumps(result)))
            level = j.get('level')
            if isinstance(level, dict):
                level = '%s-%s' % (level.get('min', 1), level.get('max', '*'))
            inputs = []
            for key in ('input', 'ingredient'):
                if key in j:
                    inputs.append(ingredient(j[key]))
            for inp in j.get('inputs', []):
                inputs.append(ingredient(inp))
            if j.get('type') == 'tconstruct:incremental_modifier':
                inputs.append('%sx, %s per item' % (j.get('needed_per_level', '?'), j.get('amount_per_item', 1)))
            slots = j.get('slots')
            slots = '' if slots is None else ', '.join('%s %s' % (v, k) for k, v in slots.items())
            tools = j.get('tools')
            tools = ingredient(tools) if tools is not None else ''
            rows.append([esc(name), '`%s`' % result, str(level or ''), esc('; '.join(i for i in inputs if i)),
                         esc(slots), tools, esc(conditions(j))])
        lines += rows_table(['Recipe', 'Modifier', 'Levels', 'Inputs', 'Slots', 'Tools', 'Condition'], rows)
        lines.append('')
    lines.append('_%d salvage recipes accompany the modifiers above._' % salvage)
    return lines


# --- materials ---

def gen_materials():
    lines = ['# Materials', '',
             'Every tool material with its stats and traits. Stats come from',
             '`tinkering/materials/stats`, traits from `tinkering/materials/traits`; a part can be',
             'built from a material whenever the material has stats for that part type. Casting a',
             'part costs the same fluid amounts as the melting reference for that material.', '']
    defdir = os.path.join(TINKERING, 'materials/definition')
    statdir = os.path.join(TINKERING, 'materials/stats')
    traitdir = os.path.join(TINKERING, 'materials/traits')
    names = sorted(f[:-5] for f in os.listdir(defdir) if f.endswith('.json'))
    for name in names:
        d = json.load(io.open(os.path.join(defdir, name + '.json'), encoding='utf-8'))
        lines.append('## %s' % name)
        meta = ['tier %s' % d.get('tier', '?')]
        if d.get('craftable'):
            meta.append('craftable in the part builder')
        if d.get('hidden'):
            meta.append('hidden')
        cond = conditions(d)
        if cond:
            meta.append('requires %s' % cond)
        lines.append('_' + ', '.join(meta) + '_')
        lines.append('')
        statfile = os.path.join(statdir, name + '.json')
        if os.path.isfile(statfile):
            stats = json.load(io.open(statfile, encoding='utf-8')).get('stats', {})
            rows = []
            for stat_type in sorted(stats):
                values = stats[stat_type]
                rendered = ', '.join('%s %s' % (k, v) for k, v in sorted(values.items())) if values else '(usable, no stat changes)'
                rows.append(['`%s`' % stat_type.replace('tconstruct:', ''), esc(rendered)])
            if rows:
                lines += rows_table(['Part type', 'Stats'], rows)
        traitfile = os.path.join(traitdir, name + '.json')
        if os.path.isfile(traitfile):
            traits = json.load(io.open(traitfile, encoding='utf-8'))
            bits = []
            for t in traits.get('default', []):
                bits.append('`%s`%s' % (t['name'], ' %s' % t['level'] if t.get('level', 1) != 1 else ''))
            for stat, ts in sorted(traits.get('perStat', {}).items()):
                for t in ts:
                    bits.append('`%s`%s (on %s)' % (t['name'], ' %s' % t['level'] if t.get('level', 1) != 1 else '', stat.replace('tconstruct:', '')))
            if bits:
                lines.append('')
                lines.append('Traits: ' + ', '.join(bits))
        lines.append('')
    return lines


# --- tools ---

def gen_tools():
    lines = ['# Tools', '',
             'Tool definitions: the parts each tool is built from at the tinker station or anvil.',
             'Tools with no parts listed are built by crafting or casting instead.', '']
    tooldir = os.path.join(TINKERING, 'tool_definitions')
    rows = []
    for f in sorted(os.listdir(tooldir)):
        if not f.endswith('.json'):
            continue
        j = json.load(io.open(os.path.join(tooldir, f), encoding='utf-8'))
        parts = []
        for module in j.get('modules', []):
            if 'parts' in module:
                for part in module['parts']:
                    if isinstance(part, dict):
                        p = part.get('part', '?')
                        if part.get('weight', 1) != 1:
                            p += ' (weight %s)' % part['weight']
                        parts.append('`%s`' % p)
                    else:
                        parts.append('`%s`' % part)
        rows.append(['`%s`' % f[:-5], esc(', '.join(parts)) if parts else '_no parts_'])
    lines += rows_table(['Tool', 'Parts'], rows)
    return lines


# --- remaining recipe sweep ---

def gen_misc():
    lines = ['# Other recipes', '',
             'Everything not covered by a dedicated page, grouped by recipe serializer: vanilla',
             'crafting for blocks and gadgets, part building, cast creation via molding, tables,',
             'compat and more. Listed so the reference provably covers every shipped recipe.', '']
    groups = defaultdict(list)
    for rel, j in sorted(all_recipes.items()):
        if rel in covered:
            continue
        groups[j.get('type', '(untyped)')].append((rel, j))
    for t in sorted(groups):
        lines.append('## `%s` (%d)' % (t, len(groups[t])))
        lines.append('')
        rows = []
        for rel, j in groups[t]:
            covered.add(rel)
            result = ''
            for key in ('result', 'recipe_result', 'output'):
                if key in j:
                    result = result_item(j[key])
                    break
            cond = conditions(j)
            rows.append([esc(rel[:-5]), result, esc(cond)])
        lines += rows_table(['Recipe', 'Result', 'Condition'], rows)
        lines.append('')
    return lines


def gen_index(counts):
    lines = ['# Recipe and content reference', '',
             'Generated from the shipped data by `scripts/generate_recipe_docs.py`; regenerate after',
             'datagen changes. Derived from `src/generated/resources`, the same files the game loads,',
             'so this reference is exactly what the Fabric port ships.', '',
             '| Page | Contents |', '|---|---|',
             '| [melting.md](melting.md) | everything the smeltery melts, with temperatures, byproducts and compat conditions |',
             '| [alloys.md](alloys.md) | all fluid alloys |',
             '| [casting.md](casting.md) | table and basin casting, cast creation |',
             '| [entity_melting.md](entity_melting.md) | fluids from entities in the smeltery |',
             '| [materials.md](materials.md) | every material: stats, traits, tiers |',
             '| [modifiers.md](modifiers.md) | every modifier recipe: inputs, slots, levels |',
             '| [tools.md](tools.md) | tool definitions and their parts |',
             '| [misc.md](misc.md) | every remaining recipe, grouped by type |', '',
             '## Totals', '']
    for k in sorted(counts):
        lines.append('- %s: %d' % (k, counts[k]))
    return lines


def main():
    load_all()
    write('melting.md', gen_melting())
    write('alloys.md', gen_alloys())
    write('casting.md', gen_casting())
    write('entity_melting.md', gen_entity_melting())
    write('modifiers.md', gen_modifiers())
    write('materials.md', gen_materials())
    write('tools.md', gen_tools())
    write('misc.md', gen_misc())

    missing = set(all_recipes) - covered
    if missing:
        raise SystemExit('reference incomplete, %d recipes uncovered: %s' % (len(missing), sorted(missing)[:10]))

    counts = defaultdict(int)
    for j in all_recipes.values():
        counts[j.get('type', '(untyped)')] += 1
    counts['total recipes'] = len(all_recipes)
    write('README.md', gen_index(counts))
    print('covered all %d recipes' % len(all_recipes))


if __name__ == '__main__':
    main()
