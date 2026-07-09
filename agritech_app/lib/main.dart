import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'config/di.dart';
import 'config/router.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize Dependency Injection
  setupDI();

  runApp(const AgritechApp());
}

class AgritechApp extends StatelessWidget {
  const AgritechApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Agritech Marketplace',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(
          seedColor: Colors.green,
          brightness: Brightness.light,
        ),
        useMaterial3: true,
        textTheme: GoogleFonts.interTextTheme(
          Theme.of(context).textTheme,
        ),
      ),
      routerConfig: appRouter,
    );
  }
}
