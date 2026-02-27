// ignore_for_file: library_private_types_in_public_api

import 'package:flutter/material.dart';
import 'package:numberpicker/numberpicker.dart';

class PagePicker extends StatefulWidget {
  const PagePicker({
    super.key,
    required this.title,
    required this.maxValue,
    required this.initialValue,
    this.numberPickerConfirmWidget,
  });

  final String title;
  final int? maxValue;
  final int? initialValue;
  final Widget? numberPickerConfirmWidget;

  @override
  _PagePickerState createState() => _PagePickerState();
}

class _PagePickerState extends State<PagePicker> {
  int? _currentValue;

  @override
  void initState() {
    _currentValue = widget.initialValue;
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: Text(widget.title),
      content: NumberPicker(
        minValue: 1,
        maxValue: widget.maxValue!,
        value: _currentValue!,
        onChanged: (value) => setState(() => _currentValue = value),
      ),
      actions: [
        TextButton(
          child: widget.numberPickerConfirmWidget ?? Text('OK'),
          onPressed: () => Navigator.of(context).pop(_currentValue),
        ),
      ],
    );
  }
}
