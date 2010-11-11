***********
MMF Planner
***********

MMF Planner is a project planning tool for projects using the Incremental
Funding Method (IFM).

IFM integrates traditional software engineering activities with financially
informed project management strategies. IFM heuristics provide clarity into
important metrics such as project level Net Present Value (NPV), Return On
Investment (ROI), initial start-up investment costs, and time needed for a
project to reach self-funding and break-even.

An IFM project consists of Minimal Marketable Features (MMFs). A MMF has
quantifiable value to the customer. Each MMF has economical figures according
to the estimated investment and earnings. A MMF can also have a precursor which
is a feature that has to be implemented before the actual MMF.


Try it!
=======

If you got Java installed, you can launch MMF Planner using Java Web Start by
opening `this file
<https://github.com/jodal/mmfplanner/raw/HEAD/dist/mmfplanner.jnlp>`_.

To start using MMF Planner, please check out our
`user guide <https://github.com/jodal/mmfplanner/wiki/User-Guide>`_.


Project history
===============

MMF Planner started as an idea at Iterate AS. It was given as an assignment to
the five students Snorre Gylterud, Stein Magnus Jodal, Johannes Knutsen, Erik
Bagge Ottesen, and Ralf Bjarne Taraldset as a part of the Customer Driven
Project at the Norwegian University of Science and Technology in the autumn of
2007.

At the end of the Customer Driven Project, MMF Planner was licensed under
GPLv2+ and released to the public. See the file ``COPYING`` for the full
license text.


Changes
=======

Version 1.1.1 - Released 2009 January 2
---------------------------------------

Enhancements:

- Added support for IFM Heuristic sort

Bug fixes:

- Fixed GC#16 (Saving results in an empty file)


Version 1.1.0 - Released 2008 November 14
-----------------------------------------

Enhancements:

- Converted build system from Ant to Maven 2.

Bug fixes:

- Fixed GC#12 (Category/MMF name is invisible when selecting row)
- Fixed GC#14 (First-time charts rendering prints stuff to System.out)
- Fixed GC#13 (Text on close button in about window is abbreviated)


Version 1.0.1 - Released 2007 November 21
-----------------------------------------

Enhancements:

- Renamed swimlane sort to pretty sort.

- NPV chart: In cases with multiple minimas, the self-funding point was set
  at the first minima, instead of the global minima.

Bug fixes:

- File open did not work because the XML parser tried to reach the XML DTD,
  which was specified as an URI in saved projects, at the local computer.
  Fixed by not adding the DTD URI in saved projects.


Version 1.0.0 - Released 2007 November 20
-----------------------------------------

Features:

- Initial public release.

