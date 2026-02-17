# MySensorApp (M5600 BLE Demo)

App Android mínimo em Kotlin para escanear, conectar e ler uma característica BLE do sensor **M5600**.

## Como gerar o APK (sem Android Studio)
1. Faça um fork ou suba este projeto para um repositório seu no GitHub.
2. Vá na aba **Actions** do repositório e habilite workflows.
3. Dispare o workflow **Build Android APK** (via *Run workflow* ou fazendo um commit/push).
4. Ao término, baixe o artefato **M5600-app** contendo o APK.

## Observações
- Este app é propositalmente simples: busca por dispositivos com nome contendo `M5600`, conecta, descobre serviços e lê a primeira característica disponível.
- Para leitura específica do M5600, substitua pelos **UUIDs** reais de serviço/características e, se necessário, aplique o parsing do payload conforme o manual do dispositivo.
- Em Android 12+ o app pedirá permissões BLE em tempo de execução (`BLUETOOTH_SCAN` / `BLUETOOTH_CONNECT`). Em versões anteriores, pode ser necessário conceder também **Localização** para *scans*.

## Licença
MIT
