import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('samples.flutter.dev/accessibility');
  bool isAccessibility = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            ElevatedButton(
              child: Text('is Accessibility'),
              onPressed: _checkAccessibility,
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _checkAccessibility() async {
    try {
      final bool result = await platform.invokeMethod('checkAccessibility');
      isAccessibility = result;
    } on PlatformException catch (e) {
      print(e);
    }

    setState(() {
      showDialog(
        context: context,
        builder: (context) {
          return CupertinoAlertDialog(
            title: Text("ユーザー補助"),
            content: isAccessibility
                ? Text("ユーザー補助がONになっています")
                : Text('ユーザー補助がOFFになっています'),
            actions: <Widget>[
              CupertinoDialogAction(
                child: isAccessibility ? Text("OK") : Text("設定はこちら"),
                onPressed: isAccessibility
                    ? () {
                        Navigator.pop(context);
                      }
                    : () {
                        Navigator.pop(context);
                        platform.invokeMethod('gotoAccessibility');
                      },
              ),
            ],
          );
        },
      );
    });
  }
}
