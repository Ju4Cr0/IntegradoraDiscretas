"""Build SVG plots and doc/experiment-report.md from data/raw.csv (standard library only)."""
import csv
import math
from pathlib import Path
from statistics import mean, stdev

ROOT = Path(__file__).resolve().parents[1]
FIGURES = ROOT / 'doc/figures'
LABELS = {'inversions': 'Inversions', 'quicksort': '3-way Quick Sort', 'closest': 'Closest pair'}
MODELS = {'n': lambda n: n, 'n log n': lambda n: n * math.log2(n), 'n²': lambda n: n * n}
COLORS = ['#146b9e', '#dd7d23', '#438a56', '#b53a4b', '#7b5ea7']


def category(n):
    """Size category as defined in the assignment."""
    if n < 10**2:
        return 'Toy'
    if n < 10**4:
        return 'Small'
    if n < 10**5:
        return 'Medium'
    return 'Large' if n >= 10**6 else 'Medium-Large'


def load():
    """Mean and standard deviation of the kept runs, per (algorithm, scenario, n)."""
    times = {}
    with open(ROOT / 'data/raw.csv', encoding='utf-8') as file:
        for row in csv.DictReader(file):
            if row['kept'] == 'true':
                key = (row['algorithm'], row['scenario'])
                times.setdefault(key, {}).setdefault(int(row['n']), []).append(float(row['milliseconds']))
    runs = len(next(iter(next(iter(times.values())).values())))
    stats = {key: [(n, mean(t), stdev(t)) for n, t in sorted(sizes.items())] for key, sizes in times.items()}
    return stats, runs


def fit(xs, ys):
    """Least squares for y = a·x + b with a, b ≥ 0. Returns (a, b, R²)."""
    k, sx, sy = len(xs), sum(xs), sum(ys)
    sxx, sxy = sum(x * x for x in xs), sum(x * y for x, y in zip(xs, ys))
    options = [(0.0, sy / k), (max(0.0, sxy / sxx), 0.0)]
    a = (k * sxy - sx * sy) / (k * sxx - sx * sx)
    if a >= 0 and sy - a * sx >= 0:
        options.append((a, (sy - a * sx) / k))
    sse = lambda p: sum((p[0] * x + p[1] - y) ** 2 for x, y in zip(xs, ys))
    a, b = min(options, key=sse)
    return a, b, 1 - sse((a, b)) / sum((y - sy / k) ** 2 for y in ys)


def grid(low, high, count=200):
    return [low * (high / low) ** (i / (count - 1)) for i in range(count)]


def svg_plot(title, x_range, y_range, series, points=None):
    """Log-log SVG chart. series: [(label, xs, ys, dashed)]; points: (xs, ys, errors)."""
    w, h, left, right, top, bottom = 760, 440, 78, 20, 44, 58
    x0, x1 = math.log2(x_range[0]), math.log2(x_range[1])
    y0, y1 = math.floor(math.log10(y_range[0])), math.ceil(math.log10(y_range[1]))
    px = lambda x: left + (math.log2(x) - x0) / (x1 - x0) * (w - left - right)
    py = lambda y: top + (y1 - math.log10(max(y, 10 ** y0))) / (y1 - y0) * (h - top - bottom)
    sup = lambda k: str(k).translate(str.maketrans('-0123456789', '⁻⁰¹²³⁴⁵⁶⁷⁸⁹'))
    out = [f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {w} {h}" font-family="Segoe UI, Arial, sans-serif" font-size="12" fill="#333">',
           f'<rect width="{w}" height="{h}" fill="white"/>',
           f'<text x="{w / 2}" y="24" text-anchor="middle" font-size="15" font-weight="600">{title}</text>']
    for k in range(math.ceil(x0), math.floor(x1) + 1, 2):
        out.append(f'<line x1="{px(2**k):.1f}" y1="{top}" x2="{px(2**k):.1f}" y2="{h - bottom}" stroke="#e3e3e3"/>'
                   f'<text x="{px(2**k):.1f}" y="{h - bottom + 18}" text-anchor="middle">2{sup(k)}</text>')
    for k in range(y0, y1 + 1):
        out.append(f'<line x1="{left}" y1="{py(10**k):.1f}" x2="{w - right}" y2="{py(10**k):.1f}" stroke="#e3e3e3"/>'
                   f'<text x="{left - 8}" y="{py(10**k) + 4:.1f}" text-anchor="end">10{sup(k)}</text>')
    out.append(f'<text x="{(left + w - right) / 2}" y="{h - 14}" text-anchor="middle">input size n</text>'
               f'<text transform="translate(18 {(top + h - bottom) / 2}) rotate(-90)" text-anchor="middle">time (ms)</text>')
    legend = []
    if points:
        for x, y, e in zip(*points):
            out.append(f'<line x1="{px(x):.1f}" y1="{py(y - e):.1f}" x2="{px(x):.1f}" y2="{py(y + e):.1f}" stroke="{COLORS[0]}"/>'
                       f'<circle cx="{px(x):.1f}" cy="{py(y):.1f}" r="4" fill="{COLORS[0]}"/>')
        legend.append(('Measured mean ± std. dev.', COLORS[0], None))
    for i, (label, xs, ys, dashed) in enumerate(series):
        color = COLORS[(i + 1 if points else i) % len(COLORS)]
        dash = ' stroke-dasharray="7 5"' if dashed else ''
        coords = ' '.join(f'{px(x):.1f},{py(y):.1f}' for x, y in zip(xs, ys))
        out.append(f'<polyline points="{coords}" fill="none" stroke="{color}" stroke-width="2"{dash}/>')
        legend.append((label, color, dash))
    for i, (label, color, dash) in enumerate(legend):
        y = top + 14 + 18 * i
        mark = (f'<circle cx="{left + 24}" cy="{y - 4}" r="4" fill="{color}"/>' if dash is None else
                f'<line x1="{left + 14}" y1="{y - 4}" x2="{left + 34}" y2="{y - 4}" stroke="{color}" stroke-width="2"{dash}/>')
        out.append(f'{mark}<text x="{left + 42}" y="{y}">{label}</text>')
    return '\n'.join(out + ['</svg>']) + '\n'


def main():
    stats, runs = load()
    FIGURES.mkdir(exist_ok=True)
    for old in FIGURES.iterdir():
        old.unlink()
    sections, summary, fitted = [], [], []
    for (algorithm, scenario), rows in stats.items():
        ns, ys, errors = zip(*rows)
        fits = {name: fit([f(n) for n in ns], ys) for name, f in MODELS.items()}
        best = max(fits, key=lambda name: fits[name][2])
        a, b, _ = fits[best]
        theory = 'n' if scenario == 'duplicates' else 'n log n'
        title = f'{LABELS[algorithm]} — {scenario}'
        xs = grid(ns[0], ns[-1])
        fitted.append((title, xs, [a * MODELS[best](x) + b for x in xs], False))
        f = MODELS[theory]
        series = [(f'Best fit: {best}', xs, fitted[-1][2], False),
                  (f'Theory: {theory} (scaled)', xs, [ys[-1] * f(x) / f(ns[-1]) for x in xs], True)]
        name = f'{algorithm}-{scenario}.svg'
        (FIGURES / name).write_text(svg_plot(title, (ns[0], ns[-1]), (min(ys), max(y + e for y, e in zip(ys, errors))),
                                             series, (ns, ys, errors)), encoding='utf-8')
        table = '\n'.join(f'| {category(n)} | {n:,} | {y:.3f} | {e:.3f} |' for n, y, e in rows)
        sections.append(f'### {title}\n\n| Category | n | Mean (ms) | Std. dev. (ms) |\n|---|---:|---:|---:|\n{table}\n\n![{title}](figures/{name})\n')
        r2 = ' | '.join(f'{fits[m][2]:.4f}' for m in MODELS)
        summary.append(f'| {title} | {theory} | {best} | {r2} | ×{ys[-1] / ys[-2]:.1f} | ×{f(ns[-1]) / f(ns[-2]):.1f} |')

    all_n = sorted({n for rows in stats.values() for n, _, _ in rows})
    values = [y for _, _, ys, _ in fitted for y in ys if y > 0]
    (FIGURES / 'comparison.svg').write_text(svg_plot('Fitted curves', (all_n[0], all_n[-1]), (min(values), max(values)), fitted),
                                            encoding='utf-8')
    environment = (ROOT / 'data/environment.txt').read_text(encoding='utf-8').strip()
    report = REPORT.format(environment=environment, sizes=', '.join(f'{n:,}' for n in all_n), runs=runs,
                           sections='\n'.join(sections), summary='\n'.join(summary),
                           before=f'{all_n[-2]:,}', after=f'{all_n[-1]:,}', models=' | '.join(f'R² {m}' for m in MODELS))
    (ROOT / 'doc/experiment-report.md').write_text(report, encoding='utf-8')
    print('\n'.join(summary))


REPORT = '''# Experiment report

## Setup

Same machine for every run, JVM with `-Xms256m -Xmx2g`:

```
{environment}
```

- **Sizes:** {sizes}. Categories follow the assignment: Toy < 10², Small < 10⁴, Medium < 10⁵, Large ≥ 10⁶.
- **Scenarios:** random and descending lists (inversions); random values and only 5 distinct values (Quick Sort); random points in a 10⁹ range (closest pair).
- **Procedure:** inputs come from a fixed seed and are saved to `data/input-*.txt`. Each input gets 5 untimed warm-up runs, then {runs} + 1 timed runs; the first timed run is discarded and the other {runs} are averaged. Raw times are in `data/raw.csv`.
- **Fitting:** least squares of `a·f(n) + b` (a, b ≥ 0) for f(n) = n, n log n and n². Each plot shows the measurements, the best fit and the theoretical curve scaled to the last point.

## Results

{sections}
### Comparison

![Fitted curves](figures/comparison.svg)

"Growth" is how much the time multiplies from n = {before} to n = {after}.

| Scenario | Theory | Best fit | {models} | Measured growth | Theoretical growth |
|---|---|---|---:|---:|---:|---:|---:|
{summary}

## Analysis

- **Pattern:** time grows steadily with n in every scenario; nothing grows exponentially.
- **Best fit vs. theory:** the best-fitting model matches the theoretical complexity in every scenario: n log n, except Quick Sort with 5 distinct values, which is linear (O(n·k) with fixed k).
- **Why every R² is high:** the largest size dominates the least-squares error, so any increasing curve near that point scores well. The growth columns and the log-log plots are better evidence.
- **Does theory predict the time?** It predicts the shape of the growth, not the milliseconds. At n ≈ 10⁶ random inputs grow somewhat faster than predicted, because immutable lists use hundreds of MB and the garbage collector works harder.
- **3-way partition:** with 5 distinct values Quick Sort is over 10× faster than on random values at the largest size.
- **Unusual sizes:** Toy times are microseconds, so clock noise dominates. Some scenarios barely change between 8,192 and 16,384 because of JIT optimisation between series.

## Sources of error

- Garbage collection pauses, which grow with memory use.
- JIT compilation may not be finished after 5 warm-up runs.
- Background processes of the operating system.
- One input per size (fixed seed).
- Quick Sort timing includes a linear pass over the output to consume the result.
'''

if __name__ == '__main__':
    main()
