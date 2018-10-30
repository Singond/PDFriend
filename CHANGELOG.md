# Changelog

### Added
- Added a new `--compact` option to the `reorder` command

## [0.6.3] - 2018-10-23
### Changed
- Fixed the installer which did not contain the PDFriend jar.

## [0.6.2] - 2018-10-18
### Changed
- Change in the build procedure. This should not affect users.

## [0.6.1] - 2018-10-12
### Added
- Added a simple installer. This installer requires Java and should run
  on both Windows and Linux.

### Changed
- Fixed an issue which caused the preprocessor settings to not be applied
  in `booklet` imposition.
- Improved the format of document pages in log messages. Logs should now
  provide more useful labels for pages like `my-document-1`, instead of
  `VirtualPage@64d9a43c`.
- Change to how PDFriend handles files internally. Previously, byte arrays
  were used; these were now substituted by data streams.

## [0.6.0] - 2018-05-23
### Added
- Added an option for double-sided `n-up` imposition.
  Use `pdfriend impose --nup --two-sided`.
- Various other features and fixes.
